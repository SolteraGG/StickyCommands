#StickyCommands

## Overview
Light-weight replacement for Essentials

- Powertools
- Player market
- Cross-server support


## Configuration

The default configuration for StickyCommands is below.

```yml
# The translation file you wish to use. (Default: messages.en_us.yml)
translation-file: "messages.en_us.yml"

worth-file: "worth.yml"
allow-selling: true
# Whether or not to automatically sell an item once listed
auto-sell: true

# Amount of time before being put into AFK mode (in seconds)
afk-timeout: 300

# Print debug statements to the console
debug: false

#The name of the server StickyCommands is running on
server: "lobby"

database:
  host: localhost
  port: 5432
  database: dbname
  username: root
  password: root
  table-prefix: stickycommands_
  # Maximum number of times postgres will try to reconnect before giving up.
  max-reconnects: 5
  # Use SSL?
  use-ssl: disabled
```

## License

StickyCommands is licensed under the MIT License.
