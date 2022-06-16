# android-base-project
A project skeleton with a generic structure and some base components that can be used as a starter for Android projects.

### Setup

First of all, clone this repo and delete `.git` folder to configure it 
for your new project.

#### Setting up KTLint & Detekt

**KTLint**

Use **brew install ktlint** to get the CLI tool
Run **`ktlint --apply-to-idea-project --android`** in terminal to set up
the formatter options when you use `cmd+shift+/` for formatting your code.

**Detekt** is already added to the project, no configuration needed.

To check the functionality, run `./gradlew x` in terminal (in the root
location of the project). `x` is a custom gradle task located in build.gradle
file which includes ktlint and detekt tasks. Run it when you need to see pre-commit
errors.

**Git hooks**

After that, copy `pre-commit` file from the root of the project to .git/hooks. 
That way every commit will have to pass ktlint and detekt checks before. You should
not avoid this step since it will be checked on CI too when the actual build process
will start.

**ALWAYS** commit your changes from android studio directly (using `cmd+K`) and have 
only `Run git hooks` checked in the right side menu of commit window. 

>When you open a pull request and all your work is done, run a gradle lint test
>from within Android Studio gradle tasks menu. This will have to pass too on CI tests

#### Circle CI

The main configuration file for CIRCLE builds is `.circle/config.yml`.
Here you can make project dependent changes.

The main workflows are:
- **flow-test**: this will run on every open pull request and is a
  required **merge check**
- **flow-release**: this will run on `develop` branch after a pull
  request is merged and will generate a new build which will be deployed
  wherever you set up in config.yml (fabric, firebase, etc)

Both workflows run `lint`, `ktlint` and `detekt`.

For **Circle CI** to run correctly, you need to make some updates on
Github/Circle CI.

**Circle CI**
- Contact the repo admin to help you and log into circle and click on
  `Setup project`
- Select `Linux` and `Gradle(Java)`
- Switch to `On` the `Only Build pull request` and `Auto-cancel
  redundant builds` sections in Project Settings -> Advances settings

**Github**
- Enable branch protections for master/develop (after all the code is
  pushed): 
  - `Require pull request review before merging`
  - `Dismiss stale pull requests on new commits`
  - `Require status checks to pass before merging`
  - `Require branches to be up to date before merging`
- Check `build_check` or `build_and_release` flow on the list of
  required statuses depending on branch preferences.