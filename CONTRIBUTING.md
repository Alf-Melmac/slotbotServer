# Contributing to Slotbot

Slotbot is released under the AGPL-3.0 license. If you would like to contribute something, or want to hack on the code
this document should help you get started.

## Using GitHub Issues

We use GitHub issues to track bugs and enhancements.
If you have a general usage question please ask on the [Discord](https://discord.gg/HSkgZNhfNK).

If you are reporting a bug, please help to speed up problem diagnosis by providing as much information as possible.

## Reporting Security Vulnerabilities

Details in [SECURITY](SECURITY.md).

## Code Conventions and Housekeeping

None of these is essential for a pull request, but they will all help. They can also be added after the original pull
request but before a merge.

- Make sure all new `.java` files have a Javadoc class comment with at least an `@author` tag identifying you, and
  preferably at least a paragraph on what the class is for.
- Add yourself as an `@author` to the `.java` files that you modify substantially (more than cosmetic changes).
- Add some Javadocs.
- A few unit tests would help a lot as well -- someone has to do it.
- If no-one else is using your branch, please rebase it against the current develop branch (or other target branch in
  the project).
- When writing a commit message please
  follow [these conventions](https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).

## Contributing workflow

- Decide on what you want to contribute.
- If you want to implement a new feature, discuss it with the [codeowners](.github/CODEOWNERS) (preferably via Discord)
  before you start coding.
- After finalizing the issue details, you can start working on the code.
- Get a code review and fix any issues that the maintainers find.
- If you can't finish your task, or change your mind - that's fine! Just let us know in the GitHub issue that you
  created in the first step of this process. The Slotbot community is friendly - we won't judge or question you if you
  decide to cancel your submission.
- Your PR is merged. You are awesome ❤️!

## Working with the Code

- Fork the repository, then clone or download your fork.
- Fill in the `application.properties`
  - spring.datasource with your database credentials (tested with PostgreSQL)
  - Create a new Discord application and bot at https://discord.com/developers/applications
  - Add the bot token, client id and client secret
- Run the main method in the SlotbotApplication class.
- Invite the bot to your server with the following link: https://discord.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&permissions=275146435584&scope=bot%20applications.commands
