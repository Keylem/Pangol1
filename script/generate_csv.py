import sys
import random
import faker
from multiprocessing import Pool, cpu_count
from math import ceil
from io import StringIO


DIRTY_RATE = 0.15

INVALID_DATES = [
    "32/13/2023", "00/00/0000", "31/02/2021", "29/02/2023",
    "13/13/2013", "2023-99-99", "not_a_date", "01/01/10000",
    "-1/-1/-1", "2020/31/12",
]

INVALID_NUMBERS = [
    "-999", "abc", "NaN", "999999", "--", "1.5.3", "inf", "", "null", "??",
]

INVALID_EMAILS = [
    "notanemail", "@nodomain", "missing@", "double@@domain.com",
    "spaces in@email.com", "nodot@com", "missing.at.sign",
]


def get_output_file(raw: str) -> str:
    return raw.strip()


def get_output_size(raw: str) -> tuple[int, bool] | tuple[None, None]:
    lower = raw.lower()
    is_datasize = lower.endswith("kb") or lower.endswith(
        "mb") or lower.endswith("gb")

    if not is_datasize:
        try:
            return int(raw), False
        except ValueError:
            return None, None

    try:
        numeric_part = raw[:-2]
        size = int(numeric_part)
        if lower.endswith("kb"):
            size *= 1024
        elif lower.endswith("mb"):
            size *= 1024 * 1024
        elif lower.endswith("gb"):
            size *= 1024 * 1024 * 1024
        return size, True
    except ValueError:
        return None, None


def dirty_name(fake: faker.Faker, rng: random.Random) -> str:
    roll = rng.random()
    if roll < 0.04:
        return ""
    if roll < 0.07:
        return "NULL"
    if roll < 0.09:
        return rng.choice(["123456", "???", "N/A", "<script>", "   "])
    return fake.name()


def dirty_email(fake: faker.Faker, rng: random.Random) -> str:
    roll = rng.random()
    if roll < 0.04:
        return ""
    if roll < 0.07:
        return "NULL"
    if roll < 0.12:
        return rng.choice(INVALID_EMAILS)
    return fake.email()


def dirty_age(fake: faker.Faker, rng: random.Random) -> str:
    roll = rng.random()
    if roll < 0.04:
        return ""
    if roll < 0.07:
        return "NULL"
    if roll < 0.12:
        return rng.choice(INVALID_NUMBERS)
    if roll < 0.14:
        return str(rng.randint(150, 999))
    return str(fake.random_int(min=18, max=80))


def dirty_date(fake: faker.Faker, rng: random.Random) -> str:
    roll = rng.random()
    if roll < 0.04:
        return ""
    if roll < 0.07:
        return "NULL"
    if roll < 0.12:
        return rng.choice(INVALID_DATES)
    return str(fake.date())


def generate_chunk_rows(args: tuple[int, int, int]) -> str:
    start_id, count, seed = args
    fake = faker.Faker()
    faker.Faker.seed(seed)
    rng = random.Random(seed)

    buffer = StringIO()
    for offset in range(count):
        row_id = start_id + offset
        name = dirty_name(fake, rng)
        email = dirty_email(fake, rng)
        age = dirty_age(fake, rng)
        date = dirty_date(fake, rng)
        buffer.write(f"{row_id},{name},{email},{age},{date}\n")

    return buffer.getvalue()


def generate_chunk_size(args: tuple[int, int, int]) -> tuple[str, int]:
    start_id, target_bytes, seed = args
    fake = faker.Faker()
    faker.Faker.seed(seed)
    rng = random.Random(seed)

    buffer = StringIO()
    current_size = 0
    id_counter = start_id

    while current_size < target_bytes:
        name = dirty_name(fake, rng)
        email = dirty_email(fake, rng)
        age = dirty_age(fake, rng)
        line = f"{id_counter},{name},{email},{age}\n"
        buffer.write(line)
        current_size += len(line.encode("utf-8"))
        id_counter += 1

    return buffer.getvalue(), id_counter - start_id


def generate_rows(output_file: str, total_rows: int):
    workers = cpu_count()
    chunk_size = ceil(total_rows / workers)

    chunks = [
        (i * chunk_size + 1, min(chunk_size, total_rows - i * chunk_size), i * 1337)
        for i in range(workers)
        if i * chunk_size < total_rows
    ]

    print(f"Generating {total_rows} rows using {workers} workers.")

    with Pool(workers) as pool:
        results = pool.map(generate_chunk_rows, chunks)

    with open(output_file, "w", buffering=8 * 1024 * 1024) as output:
        output.write("id,name,email,age,date\n")
        written = 0
        for chunk in results:
            output.write(chunk)
            written += chunk.count("\n")
            print(f"{written}/{total_rows}", end="\r")

    print(f"\nDone: {total_rows} rows written to {output_file}")


def generate_size(output_file: str, target_size: int):
    workers = cpu_count()
    chunk_target = target_size // workers

    print(f"Generating ~{target_size} bytes using {workers} workers.")

    chunks = [
        (i * 100_000 + 1, chunk_target, i * 1337)
        for i in range(workers)
    ]

    with Pool(workers) as pool:
        results = pool.map(generate_chunk_size, chunks)

    total_written = 0
    id_counter = 1

    with open(output_file, "w", buffering=8 * 1024 * 1024) as output:
        output.write("id,name,email,age\n")
        for chunk_content, _ in results:
            if total_written >= target_size:
                break
            for line in chunk_content.splitlines():
                if total_written >= target_size:
                    break
                rest = line.split(",", 1)[1] if "," in line else line
                new_line = f"{id_counter},{rest}\n"
                output.write(new_line)
                total_written += len(new_line.encode("utf-8"))
                id_counter += 1

    print(f"Done: {total_written} bytes written to {output_file}")


def main():
    args = sys.argv[1:]
    if len(args) != 2:
        print("Usage: python generate_csv.py <output_file.csv> <rows|size>")
        return 1

    output_file = get_output_file(args[0])
    if not output_file:
        print("Invalid output file name.")
        return 1

    size, is_datasize = get_output_size(args[1])
    if size is None:
        print("Invalid size. Use a number for rows or a value like 100KB / 10MB / 1GB for file size.")
        return 1

    try:
        if not is_datasize:
            generate_rows(output_file, size)
        else:
            generate_size(output_file, size)
    except KeyboardInterrupt:
        pass

    return 0


if __name__ == "__main__":
    sys.exit(main())
