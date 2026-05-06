import subprocess
import sys
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional

try:
    import javalang
except ImportError:
    print("Bibliothèque 'javalang' introuvable, installation automatique...")
    install_result = subprocess.run(
        [sys.executable, "-m", "pip", "install", "javalang"],
        check=False,
    )
    if install_result.returncode != 0:
        print("Erreur : impossible d'installer 'javalang'. Installez-le manuellement : pip install javalang")
        sys.exit(1)
    import javalang

SOURCE_DIR = Path("src/fr/univrennes/istic/l2gen")
UML_DIR = Path("uml")

VISIBILITY_SYMBOLS = {
    "public": "+",
    "protected": "#",
    "private": "-",
    "package": "~",
}


@dataclass
class FieldInfo:
    name: str
    type_name: str
    visibility: str
    is_static: bool


@dataclass
class MethodInfo:
    name: str
    return_type: str
    visibility: str
    is_static: bool
    is_abstract: bool
    parameters: list[tuple[str, str]]


@dataclass
class ClassInfo:
    name: str
    kind: str
    is_abstract: bool
    extends: Optional[str]
    implements: list[str]
    fields: list[FieldInfo]
    methods: list[MethodInfo]
    enum_constants: list[str] = field(default_factory=list)


def resolve_visibility(modifiers: set) -> str:
    for modifier in ("public", "protected", "private"):
        if modifier in modifiers:
            return modifier
    return "package"


def resolve_type_name(type_node) -> str:
    if type_node is None:
        return "void"
    if isinstance(type_node, javalang.tree.BasicType):
        return type_node.name
    if isinstance(type_node, javalang.tree.ReferenceType):
        base = type_node.name
        if type_node.arguments:
            args = ", ".join(
                resolve_type_name(arg.type) if arg.type else "?"
                for arg in type_node.arguments
            )
            return f"{base}<{args}>"
        return base
    return str(type_node)


def parse_fields(field_declarations: list) -> list[FieldInfo]:
    parsed_fields = []
    for field_decl in field_declarations:
        type_name = resolve_type_name(field_decl.type)
        visibility = resolve_visibility(field_decl.modifiers)
        is_static = "static" in field_decl.modifiers
        for declarator in field_decl.declarators:
            parsed_fields.append(FieldInfo(
                name=declarator.name,
                type_name=type_name,
                visibility=visibility,
                is_static=is_static,
            ))
    return parsed_fields


def parse_methods(method_declarations: list) -> list[MethodInfo]:
    parsed_methods = []
    for method_decl in method_declarations:
        parameters = [
            (resolve_type_name(param.type), param.name)
            for param in method_decl.parameters
        ]
        parsed_methods.append(MethodInfo(
            name=method_decl.name,
            return_type=resolve_type_name(
                getattr(method_decl, "return_type", None)),
            visibility=resolve_visibility(method_decl.modifiers),
            is_static="static" in method_decl.modifiers,
            is_abstract="abstract" in method_decl.modifiers,
            parameters=parameters,
        ))
    return parsed_methods


def parse_java_file(java_file: Path) -> list[ClassInfo]:
    source_code = java_file.read_text(encoding="utf-8", errors="replace")
    try:
        tree = javalang.parse.parse(source_code)
    except javalang.parser.JavaSyntaxError as error:
        return []

    parsed_classes = []

    for _, node in tree:
        if isinstance(node, javalang.tree.ClassDeclaration):
            parsed_classes.append(ClassInfo(
                name=node.name,
                kind="abstract class" if "abstract" in node.modifiers else "class",
                is_abstract="abstract" in node.modifiers,
                extends=node.extends.name if node.extends else None,
                implements=[
                    iface.name for iface in node.implements] if node.implements else [],
                fields=parse_fields(node.fields),
                methods=parse_methods(node.methods),
            ))

        elif isinstance(node, javalang.tree.InterfaceDeclaration):
            extended_interfaces = [
                iface.name for iface in node.extends] if node.extends else []
            parsed_classes.append(ClassInfo(
                name=node.name,
                kind="interface",
                is_abstract=True,
                extends=None,
                implements=extended_interfaces,
                fields=parse_fields(node.fields),
                methods=parse_methods(node.methods),
            ))

        elif isinstance(node, javalang.tree.EnumDeclaration):
            constants = [
                constant.name for constant in node.body.constants] if node.body.constants else []
            parsed_classes.append(ClassInfo(
                name=node.name,
                kind="enum",
                is_abstract=False,
                extends=None,
                implements=[
                    iface.name for iface in node.implements] if node.implements else [],
                fields=parse_fields(node.fields),
                methods=parse_methods(node.methods),
                enum_constants=constants,
            ))

    return parsed_classes


def format_field_puml(field_info: FieldInfo) -> str:
    visibility_symbol = VISIBILITY_SYMBOLS[field_info.visibility]
    static_modifier = "{static} " if field_info.is_static else ""
    return f"  {visibility_symbol} {static_modifier}{field_info.name} : {field_info.type_name}"


def format_method_puml(method_info: MethodInfo) -> str:
    visibility_symbol = VISIBILITY_SYMBOLS[method_info.visibility]
    static_modifier = "{static} " if method_info.is_static else ""
    abstract_modifier = "{abstract} " if method_info.is_abstract else ""
    params_str = ", ".join(
        f"{type_name} {param_name}" for type_name, param_name in method_info.parameters
    )
    return f"  {visibility_symbol} {static_modifier}{abstract_modifier}{method_info.name}({params_str}) : {method_info.return_type}"


def write_puml_file(all_classes: list[ClassInfo], puml_output_path: Path, package_name: str):
    with puml_output_path.open("w", encoding="utf-8") as puml_file:
        puml_file.write("@startuml\n")
        puml_file.write(f"title {package_name}\n\n")
        puml_file.write("skinparam classAttributeIconSize 0\n\n")

        for class_info in all_classes:
            puml_file.write(f"{class_info.kind} {class_info.name} {{\n")

            if class_info.enum_constants:
                for constant in class_info.enum_constants:
                    puml_file.write(f"  {constant}\n")
                if class_info.fields or class_info.methods:
                    puml_file.write("  --\n")

            for field_info in class_info.fields:
                puml_file.write(format_field_puml(field_info) + "\n")

            if class_info.fields and class_info.methods:
                puml_file.write("  --\n")

            for method_info in class_info.methods:
                puml_file.write(format_method_puml(method_info) + "\n")

            puml_file.write("}\n\n")

        for class_info in all_classes:
            if class_info.extends:
                puml_file.write(
                    f"{class_info.extends} <|-- {class_info.name}\n")

            for implemented_interface in class_info.implements:
                if class_info.kind == "interface":
                    puml_file.write(
                        f"{implemented_interface} <|-- {class_info.name}\n")
                else:
                    puml_file.write(
                        f"{implemented_interface} <|.. {class_info.name}\n")

        puml_file.write("\n@enduml\n")


def generate_puml(package_dir: Path, puml_output_path: Path) -> bool:
    java_files = sorted(package_dir.rglob("*.java"))
    if not java_files:
        print(f"  Aucun fichier .java trouvé dans '{package_dir}', ignoré.")
        return False

    all_classes: list[ClassInfo] = []
    for java_file in java_files:
        all_classes.extend(parse_java_file(java_file))

    if not all_classes:
        print(
            f"  Aucune classe parseable trouvée dans '{package_dir}', ignoré.")
        return False

    write_puml_file(all_classes, puml_output_path, package_dir.name)
    print(
        f"  Fichier PUML généré : '{puml_output_path}' ({len(all_classes)} classe(s))")
    return True


def get_subdirectories(directory: Path) -> list[Path]:
    return sorted(entry for entry in directory.iterdir() if entry.is_dir())


def main():
    if not SOURCE_DIR.is_dir():
        print(f"Erreur : le dossier source '{SOURCE_DIR}' est introuvable.")
        sys.exit(1)

    UML_DIR.mkdir(parents=True, exist_ok=True)

    subdirectories = get_subdirectories(SOURCE_DIR)

    if not subdirectories:
        print(f"Aucun sous-dossier trouvé dans '{SOURCE_DIR}'.")
        sys.exit(0)

    for package_dir in subdirectories:
        puml_output_path = UML_DIR / f"{package_dir.name}.puml"
        print(f"\nTraitement de '{package_dir}'...")
        generate_puml(package_dir, puml_output_path)

    print("\nTerminé.")


if __name__ == "__main__":
    main()
