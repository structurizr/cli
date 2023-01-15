# GitLab

To export your diagrams to Mermaid format in gitlab-ci, add the `workspace.dsl` to your repo and add this job to your `.gitlab-ci.yml`:

```yml
job_name:
  stage: stage_name
  image:
    name: structurizr/cli
    entrypoint: [""]
  script:
    - /usr/local/structurizr-cli/structurizr.sh export --workspace workspace.dsl --format mermaid
  artifacts:
    paths:
      - "*.mmd"
```