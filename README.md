# CyberAuth 
CyberAuth is a minecraft plugin for player authorization.
It protects from unauthorized access to the player's account.
CyberAuth prohibits unauthorized users:
- putting and interacting with blocks
- moving around
- using inventory
- hitting players
- writing messages
- execute commands

The plugin also hashes all passwords - this means that no one can find out the user's password. 

`For minecraft 1.17.x - 1.20.x`

---
**Commands:**
- login
- register
- change_password
- logout
- remove_user
- reload_cyberauth

Command **login**:
- Usage: `/login <password>`
- Description: This is the login command
- Aliases: `l`, `log` 

Command **register**:
- Usage: `/register <password> <new_password>`
- Description: This is the command to register
- Aliases: `r`, `reg`

Command **change_password**:
- Usage: `/change_password <old_password> <new_password>`
- Description: This is the command to change password

Command **logout**:
- Usage: `/logout`
- Description: This is the command to log out
- Aliases: `lg`

Command **remove_user**:
- Usage: `/removeuser <username>`
- Description: This command is for deleting a user from the database

Command **reload_cyberauth**:
- Usage: `/reload_cyberauth`
- Description: This command to reload plugin

---
### Example config.yml:
```yml
# password length
min_password_length: 3
max_password_length: 8

# time to log in (time in ticks)
auth_time: 800

# kick the player if you enter the wrong password
kick_for_wrong_password: true
```
___
### Example messages.yml:
```yml
# {%username%} - the name of the user who joined
# {%server_name%} - server name

# {%min_password_length%} - min password length
# {%min_password_length%} - max password length

# <c > - specifying the color
# <c0> - black
# <c1> - dark blue
# <c2> - dark green
# <c3> - dark aqua
# <c4> - dark red
# <c5> - dark purple
# <c6> - gold
# <c7> - gray
# <c8> - dark gray
# <c9> - blue
# <ca> - green
# <cb> - aqua
# <cc> - red
# <cd> - light purple
# <ce> - yellow
# <cf> - white

# <# > - specifying the color by hex format
# Example: <#ffffff> - white

welcome: "Welcome, {%username%} to the server!"

login:
  logged_in: "<c2>Successful login!"
  log_in: "<cc>Login, please! Usage: /login <password>"

registration:
  registered: "<c2>You are registered!"
  register_in: "<cc>Register, please! Usage: /register <password> <repeat_password>"

change_password:
  changed: "<c2>Password changed successfully!"

remove_user:
  user_removed: "<ce>{%username%}<cf> was <c4>removed<cf>!"

error:
  logged_in: "<c4>You are already logged in!"
  not_logged_in: "<c4>You are not logged in!"
  registered: "<c4>You are already registered!"
  not_registered: "<c4> You are not registered!"
  arguments: "<c4>You forgot arguments!"
  wrong_password: "<c4>Wrong password!"
  change_password: "<c4>Failed to change password!"
  set_password: "<c4>The password was entered incorrectly!"
  short_password: "<c4>The password is short! The minimum password length is {%min_password_length%}"
  long_password: "<c4>The password is too long! The maximum password length is {%max_password_length%}"
  registration: "<c4>Error during registration!"
  user_not_exist: "<c4>The user by name <ce>{%username%}<c4> does not exist."
  permissions: "<c4>You don't have permissions!"
  timeout: "<c4>Time out!"
```