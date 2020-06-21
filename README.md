## ChatApp

Customer and Support agent communication in java which works based on server socket connection.

# Compilation

1. `git clone https://github.com/HarikumarG/ChatApp.git && cd ChatApp`
2. `javac ./**/*.java`

# Steps to Run

1. Go to server folder `cd ChatApp/server`
2. Run `java ChatServer`
3. Open another terminal and go to agent folder `cd ChatApp/agent`
4. Run `java ChatAgent` (Sample credentials for login as agent username-"Hari" and password-"4039" both case-sensitive)
5. Open another terminal and go to client folder `cd ChatApp/client`
6. Run `java ChatUser` (NOTE: Can login with any user name)
7. After login as client ie.customer, go to agent logged in terminal to accept the request
