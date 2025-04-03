//登入資料
const signupButton = document.querySelector("#signup-button");
const signinButton = document.querySelector("#signin-button");
//註冊
signupButton.addEventListener("click", SignupSubmit);
//登入
signinButton.addEventListener("click", SigninSubmit);
//登入及註冊資料確認後連接對應之傳送API
async function SignupSubmit(event) {
    event.preventDefault();
    const signupInput = getSignupData();
    const inputResults = isSignupInputEmpty(signupInput);
    
    if (inputResults) {
        let signupAlert = document.querySelector("#signup-alert");
        signupAlert.textContent = "請輸入完整資訊"
        return;
    }else{
        await signup(signupInput);
    }
}

async function SigninSubmit(event) {
    event.preventDefault();
    const signinInput = getSigninData();
    const inputResults = isSigninInputEmpty(signinInput);
    
    if (inputResults) {
        let signinAlert = document.querySelector("#signin-alert");
        signinAlert.textContent = "請輸入帳號與密碼"
        return;
    }else{
        await signin(signinInput);
    }
}
//取得註冊及登入資料
function getSignupData() {
    const signupAccount = document.querySelector("input[name=account-signup]").value;
    const signupEmail = document.querySelector("input[name=e-mail]").value;
    const signupPassword = document.querySelector("input[name=password-signup]").value;
    return { 
        signupAccount: signupAccount, 
        signupEmail: signupEmail, 
        signupPassword: signupPassword, 
    };
}

function getSigninData() {
    const account = document.querySelector("input[name=account]").value;
    const password = document.querySelector("input[name=password]").value;
    return { 
        account: account,
        password: password
    };
}
//檢查註冊及登入資料是否有缺失
function isSignupInputEmpty(signupInput) {
    if (!signupInput['signupAccount'] || !signupInput['signupEmail'] || !signupInput['signupPassword']){
        return true
    }else{
        return false
    };
}
function isSigninInputEmpty(signinInput) {
    if (!signinInput['account'] || !signinInput['password']){
        return true
    }else{
        return false
    };
}
//連接後端註冊API
async function signup(signSystemInput) {
    try{
        const response = await fetchAPI("/api/user", null, "POST", signSystemInput);
        const data = await handleResponse(response);
        displaySignSystemResponse(data, null);
    }catch(error){
        handleError(error);
        displaySignSystemResponse(null, error);
    }
}
//連接後端登入API
async function signin(signSystemInput) {
    try{
        const response = await fetchAPI("/api/user/auth", null, "PUT", signSystemInput);
        const data = await handleResponse(response);
        displaySignSystemResponse(data, null);
    }catch(error){
        handleError(error);
        displaySignSystemResponse(null, error);
    }
}

//連接後端登入API
async function getMemberStatus() {
    const token = localStorage.getItem('Token');
    try{
        const getMemberData = await fetchAPI("/api/get_member_data", token, "GET");
        const data = await handleResponse(getMemberData);
        // console.log(data)
        return data
    }catch(error){
        handleError(error);
    }
}

async function fetchAPI(api, token, method, data = null) {
    let request = {
        "headers":{},
        "method":method 
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

// async function fetchAPI(api, token, method, data) {
//     let headers = {};

//     if (!(data instanceof FormData)) {
//         headers["Content-type"] = "application/json"
//     }

//     if (token){
//         headers['Authorization'] = `Bearer ${token}`
//     };

//     let request = {
//         headers: headers,
//         method: method
//     };

//     if (data instanceof FormData) {
//         request.body = data;
//     } else if (data) {
//         request.body = JSON.stringify(data);
//     };

//     const response = await fetch(api, request);
//     return response;
// }

async function handleResponse(response) {
    let contentType = response.headers.get("Content-Type");
    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message);
    };
    if (contentType.includes('application/json')) {
        return response.json();
    }
    if (contentType.includes('text/plain')) {
        return response.text();
    }
}

function handleError(error) {
    console.error('Error name: ', error);
    console.error('Error type: ', error.name);
    console.error('Error message: ', error.message);
    console.error('Stack trace: ', error.stack);
}

//後端註冊及登入回應處理
function displaySignSystemResponse(data, error) {
    let signupAlert = document.querySelector("#signup-alert");
    let signinAlert = document.querySelector("#signin-alert");
    
    if (data && data.ok) {
        signupForsuccess(signupAlert, data);
    };  

    if (data && data.token) {
        signinForsuccess(signinAlert, data);
        saveToken(data.token);
    };

    if (error) {
        switch (error.message) {
            case "Email已經註冊帳戶":
                signupForfailure(signupAlert, error.message);
                break;
            case "帳號或密碼錯誤":
                signinForfailure(signinAlert, error.message);
                break;
            case "databaseError":
                signupForfailure(signupAlert, error.message);
                signinForfailure(signinAlert, error.message);
                break;
        };
    };
}
//註冊成功文字顯示
function signupForsuccess(signupAlert, data) {
    signupAlert.innerHTML = '<div>註冊成功，請登入系統</div>'
    // console.log(data);
}
//註冊失敗文字顯示
function signupForfailure(signupAlert, error) {
    signupAlert.innerHTML = `<div>${error}</div>`
    signupAlert.style.color = "red"
}
//登入成功文字顯示
function signinForsuccess(signinAlert, data) {
    signinAlert.innerHTML = '<div>登入成功</div>'
    signinAlert.style.color = "black"
    setTimeout(() => location.reload(), 500);
    // console.log(data);
}
//登入失敗文字顯示
function signinForfailure(signinAlert, error) {
    signinAlert.innerHTML = `<div>${error}</div>`
    signinAlert.style.color = "red"
}

//token儲存
function saveToken(token){
    localStorage.setItem('Token', token);
}

//使用者登入狀態確認
async function init(){
    const token = localStorage.getItem('Token');
    if (token == null){
        if (window.location.pathname !== '/') {
            window.location.href = '/';
        }
        toggleClass('.list', 'list-toggled');
    }else{
        try{
            let signOutButtonList = document.querySelector('#signout-button-list');
            signOutButtonList.addEventListener('click', logout);
            toggleClass('#signin-button-list', 'list-sign-in-toggled');
            toggleClass('#signout-button-list', 'list-sign-out-toggled'); 
            
            const userAccountData = await fetchAPI("/api/user/auth", token, 'GET');
            const data = await handleResponse(userAccountData);
            let cashPoint = await getMemberStatus();
            showCashPointOnMenu(cashPoint);
            // console.log(data);
            // await loginCheck(data)
        }catch(error){
            handleError(error);
        }
    }
  }
  
  function showCashPointOnMenu(cashPoint){
    const cashBar = document.querySelector('#cash-point')
    cashBar.textContent = '目前點數:'+cashPoint.data.Balance+'點';
  };

  //登出
  function logout() {
    localStorage.removeItem('Token');
    if(window.location.pathname !== '/') {
        window.location.href = '/'; 
    } else {
        location.reload(); 
    }
  }
  
  //F5
  window.addEventListener('load', init);
  
  //註冊後車牌頁面導向
  window.addEventListener('load', () => {
    if (document.cookie.includes('RegistrationCompleted=TRUE')) {
        const alertContent = document.querySelector("#alert-content")
        alertContent.textContent = '請先前往"你的車車"頁面，登入至少一個車牌才可以使用停車服務喔!';
        toggleClass('#alert-page-container', 'alert-page-container-toggled');
        toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
        document.cookie = 'RegistrationCompleted=false; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    }
  });