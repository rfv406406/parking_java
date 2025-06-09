const chatMessageContainer = document.querySelector("#chat-messages-container");
const userAccount = document.querySelector("#userAccount");
const chatroomElementContainer = document.querySelector("#chatroom-element-container");
const closeChatroomIcon = document.querySelector("#returnArrow");
const messageInput = document.querySelector('#message');
const messageForm = document.querySelector('#messageForm');
const fileInput = document.querySelector("#upload-files");
const chatArea = document.querySelector(".chat-area");
const chatroomList = document.querySelector(".chatroom-list");

let userId = null;
let recipientId = null;
let parkingLotName = null;
let chatroomId = null;
let parkingLotId = null;
let subscription = null;

initChatPage();

async function initChatPage() {
    try {
        const token = tokenChecking();
        const payload = await getPayload(token);
        userId = payload.id;
        console.log("userId: " + userId);
        chatroomElementContainer.textContent = "";
        const response = await setChatroomData();
        console.log("chatroomId: "+response);
        displayMemberAccount(payload);
        displayChatroomElement();
    } catch(error) {
        handleError(error);
    }
}

messageForm.addEventListener("click", sendMessage);
closeChatroomIcon.addEventListener("click", () => {
    chatArea.style.display = "none";
    chatMessageContainer.textContent = "";
    subscription.unsubscribe();
    if (window.innerWidth < 600 && chatroomList.style.display === "none") {
        chatroomList.style.display = "flex";
    }
})

chatroomElementContainer.addEventListener("click", async (event) => {
    let chatMessagesName = document.querySelector("#chat-messages-name");
    let chatroomElement = event.target.closest('.chatroom-element');
    if (chatroomElement) {
        if (chatroomElement.id == chatroomId && chatArea.style.display == "flex") {
            return null;
        }
        if (subscription != null) {
            subscription.unsubscribe();
        }
        chatMessageContainer.textContent = "";
        const parkingLotNameData = chatroomElement.getAttribute("parkingLotName");
        chatMessagesName.textContent = parkingLotNameData;
        recipientId = chatroomElement.getAttribute("recipientId");
        parkingLotName = parkingLotNameData;
        parkingLotId = chatroomElement.getAttribute("parkingLotId");
        chatroomId = chatroomElement.id;
        console.log("chatroomId: "+chatroomId);
        const messageObjects = await getChatmessage(chatroomId);
        console.log(messageObjects)
        if (messageObjects) {
            messageObjects.forEach(messageObject => {
            displayMessage(messageObject.senderId, messageObject.message, messageObject.timestamp); 
        });
        }
        subscription = stompClient.subscribe(`/user/${chatroomId}/queue/messages`, onChatMessageReceived);
        if (window.innerWidth < 600 && chatArea.style.display === "none") {
            chatroomList.style.display = "none";
        }
        chatArea.style.display = "flex";
    }
})

function getRecipientData() {
    const url = new URL(window.location.href);
    const pathname = url.pathname;
    if (pathname === "/chatroom") {
        return null;
    }
    const pathnameArray = pathname.split("/chatroom/")[1].split("/");
    if (pathnameArray.length < 4) {
        return null;
    }
    const parkingLotNameArray = pathnameArray[3].split("%20");
    const recipientId = pathnameArray[0];
    const recipientAccount = pathnameArray[1];
    const parkingLotId = pathnameArray[2];
    let parkingLotName = "";

    for (let i=0; i<parkingLotNameArray.length; i++) {
        parkingLotName += parkingLotNameArray[i];
    }

    return {
        "recipientId" : recipientId,
        "parkingLotId" : parkingLotId
    }
}

function displayMemberAccount(payload) {
    userAccount.textContent = payload.username;
}

async function displayChatroomElement() {
    const chatroomArray = await getChatroomArray();
    if (chatroomArray) {
        for (let i=chatroomArray.length-1; i>=0; i--) {
            createChatroomElement(chatroomArray[i]);
        }
    } 
}

function createChatroomElement(data) {
    const chatroomId = data.id;
    const recipientId = data.recipientId;
    const recipientAccount = data.recipientAccount;
    const parkingLotId = data.parkingLotId; 
    const parkingLotName = data.parkingLotName;
    
    const chatroomElement = document.createElement("div");
    chatroomElement.className = "chatroom-element";
    chatroomElement.id = chatroomId;
    chatroomElement.setAttribute("recipientId", recipientId);
    chatroomElement.setAttribute("parkingLotId", parkingLotId);
    chatroomElement.setAttribute("parkingLotName", parkingLotName);
    const chatroomElementIcon = document.createElement("img");
    chatroomElementIcon.src = "/image/chatparkingimg.png";
    const chatroomName = document.createElement("div");
    chatroomName.className = "chatroom-name";   
    chatroomName.textContent = parkingLotName;

    chatroomElement.appendChild(chatroomElementIcon);
    chatroomElement.appendChild(chatroomName);
    chatroomElementContainer.appendChild(chatroomElement);
}

function getCurrentDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); 
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

function displayMessage(senderId, content, currentTime) {
    const messageContainer = document.createElement('div');
    let message;
    const timesDiv = document.createElement('div');
    timesDiv.classList.add('times-div');
    messageContainer.classList.add('message');

    if (senderId === userId) {
        messageContainer.classList.add('sender');
        timesDiv.classList.add("sender-time");
    } else {
        messageContainer.classList.add('receiver');
        timesDiv.classList.add("receiver-time");
    }

    if (typeof(content) == "string" && content.includes("d1hxt3hn1q2xo2")) {
        const img = document.createElement("img");
        message = document.createElement('div');
        message.style.overflow = "hidden";
        message.style.borderRadius = "5px";
        // img.src = URL.createObjectURL(content);
        img.src = content;
        img.style.width = "100%";
        img.style.height = "100%";
        img.style.verticalAlign = "bottom";
        img.onerror = function() {
            this.onerror = null;
            img.src = '/image/noimage.png';
        }
        img.onload = () => {
            chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
        };
        message.appendChild(img);
    } else if (typeof(content) == "string") {
        message = document.createElement('p');
        message.textContent = content;
    } else {
        return null
    }
    
    timesDiv.textContent = currentTime ? currentTime : content.timestamp;
    messageContainer.appendChild(message);
    chatMessageContainer.appendChild(messageContainer);
    chatMessageContainer.appendChild(timesDiv);
    chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
}

fileInput.addEventListener("change", () => {
    const files = fileInput.files;
    for (let i=0; i<files.length; i++) {
        if (!files[i].type.startsWith('image/')) {
            displayAlertMessage("此聊天室僅供通報使用，禁止上傳非圖片相關之檔案。");
            return null;
        }
    }
    const currentTime = getCurrentDateTime();
    const chatMessage = {
        chatroomId: chatroomId,
        senderId: userId,
        recipientId: recipientId,
        message: files,
        timestamp: currentTime
    };
    inputChatMessageToDB(chatMessage);
    fileInput.value = '';
})

async function sendMessage() {
    const currentTime = getCurrentDateTime();
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient && subscription) {
        const chatMessage = {
            chatroomId: chatroomId,
            senderId: userId,
            recipientId: recipientId,
            message: messageInput.value.trim(),
            timestamp: currentTime
        };
        stompClient.publish({ destination: '/app/chatmessage', body: JSON.stringify(chatMessage) });
        // displayMessage(userId, messageInput.value.trim(), currentTime);
        messageInput.value = '';
    } else if (messageContent) {
        const chatMessage = {
            chatroomId: chatroomId,
            senderId: userId,
            recipientId: recipientId,
            message: messageInput.value.trim(),
            timestamp: currentTime
        };
        inputChatMessageToDB(chatMessage);
        // displayMessage(userId, messageInput.value.trim(), currentTime);
        messageInput.value = '';
    }
    chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
}

async function setChatroomData() {
    const token = localStorage.getItem("token");
    const recipientData = getRecipientData();
    if (!recipientData || recipientData == null) {
        return null;
    }

    const data = {
        "senderId": userId,
        "recipientId": recipientData.recipientId,
        "parkingLotId": recipientData.parkingLotId
    }
    const response = await fetchAPI("/api/chatroom", token, "POST", data);
    const chatroomId = await handleResponse(response); 

    return chatroomId;
}

async function inputChatMessageToDB(content) {
    const token = localStorage.getItem("token");
    const maxSize = 10 * 1024 * 1024;
    const alertMessage = "請選擇小於 10MB 的檔案，總檔案物超過20 MB!";
    let formData = new FormData();
    formData.append("chatroomId", content.chatroomId);
    formData.append("senderId", content.senderId);
    formData.append("recipientId", content.recipientId);
    formData.append("timestamp", content.timestamp);
    if (content.message instanceof FileList) {
        if (content.message.size > maxSize * 2) {
            displayAlertMessage(alertMessage);
            return null;
        }
        for(let i=0; i<content.message.length; i++) {
            if (content.message[i].size > maxSize) {
                displayAlertMessage(alertMessage);
                return null;
            }
            formData.append("img", content.message[i]);
        }
    } else if (typeof(content.message) == "string") {
        formData.append("message", content.message);
    } else {
        return null;
    }
   
    const response = await fetchAPI("/api/chatmessage", token, "POST", formData);
    const data = await handleResponse(response);
    console.log(data)
}

async function getChatmessage(chatroomId) {
    const token = localStorage.getItem("token");
    const response = await fetchAPI(`/api/chatmessage/${chatroomId}`, token, "GET");
    const data = await handleResponse(response);
    return data;
}

async function getChatroomArray() {
    const token = localStorage.getItem("token");
    const response = await fetchAPI(`/api/chatroom`, token, "GET");
    const data = await handleResponse(response);
    return data;
}

function applyLayout() {
  if (window.innerWidth < 600) {
    if (chatArea.style.display === "flex") {
        chatroomList.style.display = "none";
    } else {
        chatroomList.style.display = "flex";
    }
  } 

  if (window.innerWidth >= 600) {
    chatArea.style.display === "flex"
    chatroomList.style.display = "flex";
  }
}

applyLayout();

window.addEventListener('resize', applyLayout);

