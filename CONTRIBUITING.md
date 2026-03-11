# Contributing to Traveler App

We use a feature-branching strategy to keep our `main` branch stable and safe.

## Branching Strategy
* **main**: This branch contains the production-ready code. No one should commit directly to `main`.
* **develop**: The main integration branch. All features are merged here first and after tests, they are merged with main.
* **feature/**: New features or tasks should be branches from develop and named `feature/description` (e.g., `feature/login-screen`).

## How to Contribute
1. Create a branch from `develop` with an appropriated name.
2. Commit your changes with clear messages.
3. Open a Pull Request to merge your branch back into `develop`.
4. Once tested, `develop` will be merged into `main` for releases.