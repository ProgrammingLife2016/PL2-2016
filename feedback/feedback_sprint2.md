# Feedback PL2 Pantzerfaust

|  | Product Planning |  |  |  |  |
|-------------------------------------|------------------|----------------|---------------|---------|---------|
|  |  |  | User Stories |  |  |
|  | Analysis | Prioritisation | Specification | Clarity | Roadmap |
| PL2 (Pantzerfaust) | 8 | 8 | 8 | 8 | 7 |

|  | Sprint 2 |  |  |  |  |  |
|-------------------------------------|------------|-----------|----------------|-----------------------|----------------|------------|
|  | User Story |  |  | Learning From History |  |  |
| Group | Definition | Splitting | Responsibility | Estimation | Prioritisation | Reflection |
| PL2 (Pantzerfaust) | 10 | 7 | 9 | 6 | 8 | 7 |

|  | Code Evolution Quality 2 |  |  |  |  |  |  |  |  |  |  |
|-------------------------------------|--------------------------|---------|---------------------|------------------|--------|----------|------------------------|---------|---------|------------------------|-------------|
|  | Architecture |  |  | Code Readability |  |  | Continuous Integration |  |  | Pull-based Development |  |
| Group | Changes | Arch-DD | Code Change Quality | Formatting | Naming | Comments | Building | Testing | Tooling | Branching | Code Review |
| PL2 (Pantzerfaust) | 8 | 7 | 8 | 8 | 7 | 7 | 5 | 7 | 7 | 10 | 9 |

As promised the changes in your grades. Here is some feedback:

## Changed tooling to 7
You should configure the root to display all project reports (Jacoco and Findbugs are not shown, whereas PMD and CPD are not shown in the modules).
Besides that Findbugs and PMD show errors that should be fixed.

## Changed testing to 7
The entire Model class is untested and utility is not above 80%.

## EAD Feedback
- Should be a more formal document. (Front cover with team members, overall layout)
- Performance should not only be about preprocessing, although that is one point to mention.
- At consistency, why mention bubbles? Also it should be consistent in more ways than only zooming.
- At code quality, be more assertive in what you will use instead of 'some tools'
- Missing point on always having a working version.
- In chapter Architecture Design there should be more references to the earlier set design goals. 
- Missing choice of design patterns
- Layout of Testing and Quality Control is out of place

## Grade EAD Changes
- User interface is inserting data, nothing on that in EAD
- In the document it's not clear what part is on the server side or the client side.
- How to interpret the EAD? Parser == GraphVisualizer? 

Changed grade to 8, since these changes happened this week.

## Grade Code Quality
- Controllers used for JavaFX should be initialisables 
- Hardcoding file paths
- Use a static class, loads of magic numbers
- In DrawGraph, using streams is neater for drawing edges.
- Lack of design patterns


Please prepare next meeting with questions.