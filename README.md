<h1 align="center">
  <img src="docs/images/logo.png" alt="Pangol1 Logo" width="64"/>
  <br/>
  Pangol1
</h1>

<p align="center">
  <b>High-performance SVG reporting engine for massive tabular datasets</b>
</p>

<p align="center">
  Generate reports and SVG charts from CSV, TSV, Parquet and more built for very largedatasets.
</p>

<p align="center">
  <img src="https://img.shields.io/github/languages/top/jules1univ/Pangol1" alt="Language"/>
  <img src="https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-purple" alt="Platform"/>
  <img src="https://img.shields.io/badge/Charts-SVG-green" alt="Charts"/>
  <a href="https://github.com/jules1univ/Pangol1/actions/workflows/build-jar.yml">
    <img src="https://github.com/jules1univ/Pangol1/actions/workflows/build-jar.yml/badge.svg" alt="Build"/>
  </a>
  <a href="https://github.com/jules1univ/Pangol1/actions/workflows/test-junit.yml">
    <img src="https://github.com/jules1univ/Pangol1/actions/workflows/test-junit.yml/badge.svg" alt="Tests"/>
  </a>
</p>

![MainView](docs/images/main_with_chart.png)

## Features

- 📊 Generate **SVG charts** from tabular data
- 📄 Create complete analytical reports
- ⚡ Handle **massive datasets using Parquet**
- 🗂 Support multiple data sources:
  - CSV
  - TSV
  - TXT
  - XLSX
  - JSON
  - XML
  - Parquet
- 🖼 Export your charts to SVG
- 📑 Export your reports to HTML, PDF or Markdown

## Why Pangol1?

Most reporting tools struggle with very large datasets or generate heavy visual outputs.

Pangol1 is designed to:

- process large tabular files efficiently,
- generate lightweight SVG visualizations,
- produce portable reports suitable for analytics workflows.

**Massive data in, lightweight visuals out.**

## Requirements

Before running Pangol1, install:

- **Java JDK 21+**
- **Git**
- **VS Code** (recommended)

Check Java installation:

```bash
java --version
```

## Installation

Clone repository:

```bash
git clone https://github.com/jules1univ/Pangol1.git
cd Pangol1
```

## Build & Run

Compile manually:

```bash
javac -cp "lib/*" application/Pangol1.java
```

Run:

```bash
java -cp ".:lib/*" application.Pangol1
```

Or use generated JAR:

```bash
java -jar Pangol1.jar
```

## Dependencies

Pangol1 uses:

- **FlatLaf** — modern Java UI
- **DuckDB JDBC** — in-memory analytical database
- **JUnit** — testing
- **Batik** — SVG → PNG conversion
- **OpenHTMLToPDF** — HTML → PDF export
- **JavaFX** — HTML report rendering

## Documentation

- [Full Documentation](docs/DOCUMENTATION.md)
- [Contributing Guide](CONTRIBUTING.md)
- [Project Members](docs/MEMBERS.md)

## License

Distributed under the MIT License.

See [LICENSE](LICENSE) for more information.
