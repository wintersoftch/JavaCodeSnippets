# JavaCodeSnippets

[![Build Status](https://github.com/wintersoftch/JavaCodeSnippets/actions/workflows/wf-push-build.yml/badge.svg)](https://github.com/wintersoftch/JavaCodeSnippets/actions/workflows/wf-push-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=wintersoftch_JavaCodeSnippets&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=wintersoftch_JavaCodeSnippets)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

JavaCodeSnippets is a personal **playground** repository with small, focused Java examples collected while learning and
experimenting with the language and ecosystem.

## Purpose

This repository serves as my personal playground for Java snippets, patterns, and API explorations. It's primarily for
educational use to:

- Document small, self-contained examples while learning Java features
- Experiment with language constructs, libraries, and tools
- Maintain a reference collection for future projects
- Practice CI/CD pipelines, static analysis, and build automation

> **Note**: This is not a production library. Code quality varies intentionally as part of the learning process.

## Build

The project uses Maven as the build tool (standard `pom.xml` layout is assumed).

## Quick Start

### Clone and build

```bash
git clone https://github.com/wintersoftch/JavaCodeSnippets.git
cd JavaCodeSnippets
mvn clean install
```

### Run tests

`mvn test`

## Structure

```text
.
├── AWS
│   └── src
│       └── main/java/ch/wintersoft/java/aws
│           ├── alb    # AWS application load balancer
│           ├── batch  # AWS batch processing
│           └── s3     # AWS S3 storage
└── Java
    └── src
        └── main/java/ch/wintersoft/java/snippets
            ├── jbbp     # JBBP library examples
            │   └── moxa # Specific Moxa implementation
            └── lang     # General Java constructs
```

## Build & Quality

**Maven** handles builds with standard lifecycle phases.

**SonarCloud** provides static analysis for learning code quality practices.

## Usage

- Browse `src/main/java` for copy-paste examples
- Run tests to see snippets in action
- Adapt examples to your projects
- Contribute your own snippets via PRs (educational ones welcome!)

## Support My Work

If you like this project and find it is helpful for your case, please do not hesitate to buy me a coffee.

[!["Buy Me A Coffee"](https://buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/wintersoftch)

## License

MIT License - see [LICENSE](LICENSE) file.
