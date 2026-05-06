import platform
import shutil
import subprocess
import sys
from pathlib import Path

PLANTUML_JAR = Path("script/plantuml.jar")
UML_DIR = Path("uml")


def check_java():
    if shutil.which("java") is None:
        print("Erreur : Java est introuvable. Installez un JRE et assurez-vous qu'il est dans le PATH.")
        sys.exit(1)


def check_plantuml_jar():
    if not PLANTUML_JAR.is_file():
        print(f"Erreur : plantuml.jar introuvable à '{PLANTUML_JAR}'")
        sys.exit(1)


def check_graphviz():
    if shutil.which("dot") is None and platform.system().lower() == "linux":
        print("Graphviz non trouvé, tentative d'installation automatique...")
        result_update = subprocess.run(["sudo", "apt", "update"], check=False)
        result_install = subprocess.run(
            ["sudo", "apt", "install", "-y", "graphviz"], check=False)
        if result_update.returncode != 0 or result_install.returncode != 0:
            print(
                f"Erreur : impossible d'installer Graphviz automatiquement.\n{install_hint}")
            sys.exit(1)


def generate_svg(puml_path: Path):
    result = subprocess.run(
        ["java", "-jar", str(PLANTUML_JAR), "-tsvg", str(puml_path)],
        check=False,
    )
    if result.returncode == 0:
        svg_path = puml_path.with_suffix(".svg")
        print(f"  Fichier SVG généré  : '{svg_path}'")
    else:
        print(f"  Erreur lors de la génération du SVG pour '{puml_path}'")


def main():
    check_java()
    check_plantuml_jar()
    check_graphviz()

    if not UML_DIR.is_dir():
        print(
            f"Erreur : le dossier '{UML_DIR}' est introuvable. Lancez d'abord generate_puml.py.")
        sys.exit(1)

    puml_files = sorted(UML_DIR.glob("*.puml"))

    if not puml_files:
        print(f"Aucun fichier .puml trouvé dans '{UML_DIR}'.")
        sys.exit(0)

    for puml_path in puml_files:
        print(f"\nTraitement de '{puml_path}'...")
        generate_svg(puml_path)

    print("\nTerminé.")


if __name__ == "__main__":
    main()
