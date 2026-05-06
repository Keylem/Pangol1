import shutil
import subprocess
import sys
from pathlib import Path

SOURCE_DIR = Path("src")
DOC_DIR = Path("doc")
CLASSPATH_OUTPUT_FILE = Path("target/classpath.txt")


def check_tool(tool_name):
    if shutil.which(tool_name) is None:
        print(
            f"Erreur : '{tool_name}' est introuvable. Assurez-vous qu'il est installé et dans le PATH.")
        sys.exit(1)


def resolve_maven_classpath():
    print("Résolution des dépendances Maven...")

    CLASSPATH_OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)

    mvn_executable = "mvn.cmd" if sys.platform == "win32" else "mvn"

    result = subprocess.run(
        [
            mvn_executable,
            "dependency:build-classpath",
            f"-Dmdep.outputFile={CLASSPATH_OUTPUT_FILE.resolve()}",
            "-q",
        ],
        check=False,
    )

    if result.returncode != 0:
        print("Erreur : impossible de résoudre les dépendances Maven.")
        sys.exit(result.returncode)

    if not CLASSPATH_OUTPUT_FILE.is_file():
        print(
            f"Erreur : le fichier de classpath '{CLASSPATH_OUTPUT_FILE}' n'a pas été créé.")
        sys.exit(1)

    classpath = CLASSPATH_OUTPUT_FILE.read_text(encoding="utf-8").strip()
    separator = ";" if sys.platform == "win32" else ":"
    print(f"Classpath résolu ({len(classpath.split(separator))} entrées).")
    return classpath


def generate_javadoc(classpath):
    print(
        f"Génération de la JavaDoc depuis '{SOURCE_DIR}' vers '{DOC_DIR}'...")

    DOC_DIR.mkdir(parents=True, exist_ok=True)

    result = subprocess.run(
        [
            "javadoc",
            "-d", str(DOC_DIR),
            "-sourcepath", str(SOURCE_DIR),
            "-subpackages", "fr",
            "-classpath", classpath,
            "-encoding", "UTF-8",
            "-charset", "UTF-8",
        ],
        check=False,
    )

    if result.returncode == 0:
        print(
            f"Documentation générée avec succès dans le dossier '{DOC_DIR}' !")
    else:
        print("Erreur lors de la génération de la documentation !")
        sys.exit(result.returncode)


def main():
    check_tool("javadoc")

    mvn_executable = "mvn.cmd" if sys.platform == "win32" else "mvn"
    check_tool(mvn_executable)

    if not SOURCE_DIR.is_dir():
        print(f"Erreur : le dossier source '{SOURCE_DIR}' est introuvable.")
        sys.exit(1)

    if not Path("pom.xml").is_file():
        print("Erreur : aucun fichier 'pom.xml' trouvé. Ce script doit être lancé depuis la racine du projet Maven.")
        sys.exit(1)

    classpath = resolve_maven_classpath()
    generate_javadoc(classpath)


if __name__ == "__main__":
    main()
