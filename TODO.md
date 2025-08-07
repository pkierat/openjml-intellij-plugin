# TODO – OpenJML IntelliJ Plugin

## Minimum Viable Product (MVP)
* [x] Generate JML specification file from a Java class
* [x] Support 'src/main/jml' as a spec location
* [x] Support 'src/main/java' as a default spec location
* [ ] Generate JML specific ation for a method
* [x] Add syntax highlighting for Java code in JML files
* [ ] Add syntax highlighting for JML comments (`//@`, `/*@ ... @*/`)
* [x] Integrate OpenJML as a backend process
* [ ] Enable manual verification trigger ("Verify with OpenJML")
* [x] Display verification results and errors inline in the editor

## Advanced Features
* [ ] Autocomplete for JML constructs (`requires`, `ensures`, `assignable`, etc.)
* [x] Navigation from Java files to JML files and back
* [ ] Navigation from JML references to Java declarations
* [ ] Show contracts in tooltips on method/property usage
* [x] Project-level configuration panel for OpenJML path and options
* [ ] Real-time inspections for missing or invalid JML annotations

## User Experience Enhancements
* [ ] Dedicated tool window for verification output (like Run/Problems)
* [ ] Structured display of verification results (package → class → method)
* [ ] Quick Fixes: Generate default or missing JML contracts
* [ ] Live templates for common JML patterns

## Optional / Future Additions
* [ ] Support for frame conditions visualization (`assignable`, `modifiable`)
* [ ] Export verification results to JSON/XML
* [ ] CI integration hooks (e.g., GitHub Actions, Jenkins)
* [ ] JML wizard or specification generation assistant

