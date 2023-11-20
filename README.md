# Chatting Between Peers

This is a Peer to Peer Application that runs on a Network.

# 1

In this part, you are required to write Peer-to-peer Application Chatting Java UDP Socket
Programming:

- Write a GUI application by using Java.
- Your program should have Text boxes, Buttons, Text Areas, Drop-Downs etc.
- You should enter Source and destination IP addresses, port numbers.
- Also, you should display sent and received messages.
- Blocking Code is Not Accepted.

## Extra Requirements:

- Add the timestamp for each exchanged message (for both sent and received)
- Use RED color for sent messages
- Use GREEN color for received messages
- Add button to delete selected message or delete all conversation from both sides.
- Replicate the same task by using TCP Socket instead of UDP Socket.

# 2

In this part you are required to apply TCP Java Socket programming, but we will add Two Parts:

## 2.1

A TCP Client to the Application described in 1.

- This Client requires the additions of buttons to register to the TCP Server Described in 2.2.
- The Server will simply keep a list of UDP clients involved in the Chatting.
- The TCP Server will send a message to each TCP client in the Active Chatting Client described in 1 to inform it of
  the List of Active Clients. This is similar to Skype and other chatting Servers.
- The server just keeps a list of those that Active Chatting Clients. So, you will need to modify the code in 1 to
  accommodate this Requirement. The Actual chatting will remain peer-to-peer. But the User chooses which Client to
  talk to from the List Provided by the Server.
- The login in the Clients is used for Registration to the TCP server to keep track of online Clients. You Should
  display the List of Active Client.

## 2.2

Add a TCP Server that keeps track of the Active Chatting Clients as described in 2.1. It should
Show a GUI with the Active List of Clients. The login in the Clients is used for Registration.

## Extra Requirements:

- Enable sent to all option
- Add the name for each online user next to its corresponding IP and port number i.e. Ali 192.168.1.25 600
- Add file at the TCP server side, which contains the credential for users, login information as username and
  password as follows:

- | Username | Password |
  |-----------|----------|
  | Ali | 1234 |
  | Saly | A20B |
  | Aws | ABcd |
  | Adam | 1Cb2 |

- Both username and password must be valid for login process, if OK, then the
  client can proceed and a notification can appear as (Logged in successful), else, a
  message will appear (invalid login information, either username or password)
- enable the logout button.
- Use different test color for each user.
- Consider the username and passwords are not case sensitive. i.e. Ali = ali = aLi = …
