let stompClient = null;
let allReadOnlyRoomId = null;

const chatLineElementId = "chatLine";
const roomIdElementId = "roomId";
const messageElementId = "message";

document.addEventListener("DOMContentLoaded", () => {
    loadConfig().then(() => {
    });
});

const loadConfig = async () => {
    try {
        const response = await fetch('/config');
        const config = await response.json();
        allReadOnlyRoomId = config.allReadOnlyRoomId;
        console.log("Loaded allReadOnlyRoomId:", allReadOnlyRoomId);
    } catch (error) {
        console.error("Ошибка загрузки конфигурации:", error);
    }
}

const setConnected = (connected) => {
    const connectBtn = document.getElementById("connect");
    const disconnectBtn = document.getElementById("disconnect");

    connectBtn.disabled = connected;
    disconnectBtn.disabled = !connected;
    const chatLine = document.getElementById(chatLineElementId);
    chatLine.hidden = !connected;
}

const connect = () => {
    stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
    stompClient.connect({}, (frame) => {
        setConnected(true);
        const userName = frame.headers["user-name"];
        const roomId = document.getElementById(roomIdElementId).value;

        if (roomId === allReadOnlyRoomId) {
            alert(`Комната ${allReadOnlyRoomId} доступна только для чтения. Отправка сообщений запрещена.`);
            document.getElementById(messageElementId).disabled = true;
            document.getElementById("send").disabled = true;
        } else {
            document.getElementById(messageElementId).disabled = false;
            document.getElementById("send").disabled = false;
        }

        console.log(`Connected to roomId: ${roomId} frame:${frame}`);
        const topicName = `/topic/response.${roomId}`;
        const topicNameUser = `/user/${userName}${topicName}`;
        stompClient.subscribe(topicName, (message) => showMessage(JSON.parse(message.body).messageStr));
        stompClient.subscribe(topicNameUser, (message) => showMessage(JSON.parse(message.body).messageStr));
    });
}

const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

const sendMsg = () => {
    const roomId = document.getElementById(roomIdElementId).value;
    const message = document.getElementById(messageElementId).value;

    if (roomId === allReadOnlyRoomId) {
        alert(`Отправка сообщений в комнату ${allReadOnlyRoomId} запрещена.`);
        return;
    }

    stompClient.send(`/app/message.${roomId}`, {}, JSON.stringify({'messageStr': message}))
}

const showMessage = (message) => {
    const chatLine = document.getElementById(chatLineElementId);
    let newRow = chatLine.insertRow(-1);
    let newCell = newRow.insertCell(0);
    let newText = document.createTextNode(message);
    newCell.appendChild(newText);
}
