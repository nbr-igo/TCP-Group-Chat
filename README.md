# TCP Group Chat
A multi-threaded Java chat application using sockets that supports real-time group messaging and dynamic chat rooms.
### 🚀 Features<br/>
🧵 Multi-threaded server (each client runs on its own thread)<br/>
🌍 Global default chat room<br/>
🏠 Create and join custom chat rooms<br/>
🔁 Switch rooms using commands<br/>
❌ Graceful exit with /exit<br/>
🔊 Real-time message broadcasting<br/>
🛑 Automatic cleanup on disconnect<br/>
🏗️ Architecture Overview<br/>
🖥️ Server Side<br/>

### 📚 Concepts Used<br/>
Java Sockets (ServerSocket, Socket)<br/>
Multi-threading (Runnable, Thread)<br/>
Buffered I/O Streams<br/>
Shared static collections<br/>
Command parsing<br/>
Resource cleanup & exception handling<br/>