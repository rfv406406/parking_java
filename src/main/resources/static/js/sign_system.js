const signupButton = document.querySelector("#signup-button");
const signinButton = document.querySelector("#signin-button");
const signonGo = document.querySelector("#go-signon");
const returnSignin = document.querySelector("#return-signin");
//註冊
signupButton.addEventListener("click", signupSubmit);
//登入
signinButton.addEventListener("click", signinSubmit);
//切換註冊
signonGo.addEventListener("click", () => {
    let signinAlert = document.querySelector("#signin-alert");
    signinAlert.textContent = "";
})
//切換登入
returnSignin.addEventListener("click", () => {
    let signupAlert = document.querySelector("#signup-alert");
    signupAlert.textContent = "";
})


async function signupSubmit(event) {
    event.preventDefault();
    const signupAccount = document.querySelector("input[name=account-signup]").value;
    const signupEmail = document.querySelector("input[name=e-mail]").value;
    const signupPassword = document.querySelector("input[name=password-signup]").value;

    const signupData = {
        "account": signupAccount,
        "password": signupPassword,
        "email": signupEmail
    };
    
    if (!signupAccount || !signupEmail || !signupPassword) {
        let signupAlert = document.querySelector("#signup-alert");
        signupAlert.textContent = "請輸入完整資訊"
        signupAlert.style.color = "red";
        return null;
    }

    try{
        const response = await fetchAPI("/api/member", null, "POST", signupData);
        const data = await handleResponse(response);
        displaySignSystemResponse(data, null);
    }catch(error){
        handleError(error);
        displaySignSystemResponse(null, error);
    }
}

async function signinSubmit(event) {
    event.preventDefault();
    const account = document.querySelector("input[name=account]").value;
    const password = document.querySelector("input[name=password]").value;
    const signinData = {
        "account": account,
        "password": password
    }
    
    if (!account || !password) {
        let signinAlert = document.querySelector("#signin-alert");
        signinAlert.textContent = "請輸入帳號與密碼";
        signinAlert.style.color = "red";
        return null;
    }

    try{
        const response = await fetchAPI("/api/member/login", null, "POST", signinData);
        const data = await handleResponse(response);
        displaySignSystemResponse(data, null);
    }catch(error){
        handleError(error);
        displaySignSystemResponse(null, error);
    }
}

//連接後端登入API
async function getMemberBalanceStatus() {
    const token = tokenChecking();
    try{
        const getMemberData = await fetchAPI("/api/member/balanceStatus", token, "GET");
        const data = await handleResponse(getMemberData);
        // console.log(data)
        return data
    }catch(error){
        handleError(error);
    }
}

async function fetchAPI(api, token, method, data = null) {
    //credentials cookie設置
    let request = {
        "headers":{},
        "method":method,
        credentials:"include", 
    }

    if (token){
        request.headers['Authorization'] = `Bearer ${token}`
    };

    if (data != null) {
        if (data instanceof FormData) {
            request.body = data;
        } else {
            request.headers["Content-type"] = "application/json"
            request.body = JSON.stringify(data);
        };
    }
    
    const response = await fetch(api, request);
    return response;
}

async function handleResponse(response) {
    let contentType = response.headers.get("Content-Type") || "";
    if (!response.ok) {
        let errorData;
        if (contentType.includes('application/json')) {
            errorData = await response.json();
            throw new Error(errorData.message);
        }
        if (contentType.includes('text/plain')) {
            errorData = await response.text();
            throw new Error(errorData);
        }
    };
    
    if (contentType.includes('application/json')) {
        return await response.json();
    }
    if (contentType.includes('text/plain')) {
        return await response.text();
    }

    return null;
}

function handleError(error) {
    console.error('Error name: ', error);
    console.error('Error type: ', error.name);
    console.error('Error message: ', error.message);
    console.error('Stack trace: ', error.stack);
}

//後端註冊及登入回應處理
async function displaySignSystemResponse(data, error) {
    let signupAlert = document.querySelector("#signup-alert");
    let signinAlert = document.querySelector("#signin-alert");
    if (data == 'OK') {
        signupAlert.textContent = "註冊成功，請登入系統!";
        signupAlert.style.color = "green";
    };  
    if (data && data.token) {
        signinAlert.textContent = "登入成功!";
        signinAlert.style.color = "green";
        localStorage.setItem('token', data.token);
        setTimeout(() => toggleSignIn(), 500);
        await init();
        //顯示初次登入訊息
        setTimeout(() => firstLoggingMessage(), 1000);
    };
    if (error) {
        switch (true) {
            case error.message.includes("該帳號已被使用!"): {
                signupAlert.textContent = "該帳號已被使用!";
                signupAlert.style.color = "red";
                break;
            }
            case error.message.includes("Email已經註冊帳戶"): {
                signupAlert.textContent = "Email已經註冊帳戶";
                signupAlert.style.color = "red";
                break;
            }
            case error.message.includes("帳號或密碼錯誤"): {
                signinAlert.textContent = "帳號或密碼錯誤";
                signinAlert.style.color = "red";
                break;
            }
            case error.message.includes("databaseError"): {
                signupAlert.textContent = "databaseError";
                signupAlert.style.color = "red";
                signinAlert.textContent = "databaseError";
                signinAlert.style.color = "red";
                break;
            }
            case error.message.includes("帳號或密碼錯誤"): {
                signinAlert.textContent = "帳號或密碼錯誤";
                signinAlert.style.color = "red";
                break;
            }
        };
    };
}

//使用者登入狀態確認
async function init(){
    const token = tokenChecking();
    if (token == null) {
        return null;
    }
    try{
        const signOutButtonList = document.querySelector('#signout-button-list');
        signOutButtonList.addEventListener('click', logout);
        toggleClass('#signin-button-list', 'list-sign-in-toggled');
        toggleClass('#signout-button-list', 'list-sign-out-toggled'); 

        const payload = await getPayload(token);
        if (payload == null) {
            logout();
        }
        const userAccount = payload.username;
        const balance = await getMemberBalanceStatus();
        showCashPointOnMenu(userAccount, balance);
        const headList = document.querySelectorAll(".list");
        headList.forEach(item => {
            item.style.display = "flex";
        });
        if (location.hash === '#parkingPage') {
            parkingPageButtonList();
            window.location.hash = "";  
        }
        webSocketConnect();
    }catch(error){
        handleError(error);
    }
}
  
function showCashPointOnMenu(payload, balance){
    const cashBar = document.querySelector('#cash-point');
    const payloadDiv = document.createElement("div");
    const balanceDiv = document.createElement("div");
    balanceDiv.id = "balanceDiv";
    if (balance == null) {
        payloadDiv.textContent =`您好 ${payload}，您的點數目前為`;
        balanceDiv.textContent = ` ${0}點。`; 
    } else {
        payloadDiv.textContent =`您好 ${payload}，您的點數目前為`;
        balanceDiv.textContent = ` ${balance.balance}點。`; 
    }
    cashBar.appendChild(payloadDiv);
    cashBar.appendChild(balanceDiv);
};

function tokenChecking() {
    let token = localStorage.getItem('token');
    if (token == null){
        if (window.location.pathname !== '/') {
            window.location.href = '/';
        }
        return null;
    }
    return token;
}

async function getPayload(token) {
    const userAccountData = await fetchAPI("/api/member/auth", token, 'GET');
    let payload = await handleResponse(userAccountData);
    return payload;
}

//登出
function logout() {
    localStorage.removeItem('token');
    if(window.location.pathname !== '/') {
        window.location.href = '/'; 
    } else {
        location.reload(); 
    }
}
  
//F5
window.addEventListener('load', init);
  
function firstLoggingMessage() {
    if (document.cookie.includes('RegistrationCompleted=TRUE')) {
        const errorMessage = '請先前往"你的車車"頁面，登入至少一個車牌才可以使用停車服務喔!';
        displayAlertMessage(errorMessage);
        document.cookie = 'RegistrationCompleted=; Max-Age=0; path=/;';
    }
}

function displayAlertMessage(alertMessage) {
    const alertContainer = document.querySelector("#alert-page-container");
    const alertContent = alertContainer.querySelector("#alert-content");
    const alertButton = alertContainer.querySelector("#alert-content-checked-button");

    alertContent.textContent = alertMessage;
    toggleClass('#alert-page-container', 'alert-page-container-toggled');
    toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');

    return alertButton;
}