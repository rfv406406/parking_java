fetchIdPageData();
async function fetchIdPageData(){
    try{
      const token = localStorage.getItem('Token');
      const response = await fetchAPI("/api/member/memberDetails", token, 'GET');
      const data = await handleResponse(response);
      idDataInTableAndInput(data);
    }catch(error){
      handleError(error);
    }
}

function idDataInTableAndInput(data){
  document.querySelector('#id_page_id_number').textContent = data.id || '無';
  document.querySelector('#id_page_account').textContent = data.account || '無';
  document.querySelector('#id_page_name').textContent = data.name || '無';
  document.querySelector('#id_page_e-mail').textContent = data.email || '無';
  document.querySelector('#id_page_birthday').textContent = data.birthday || '無';
  document.querySelector('#id_page_phone').textContent = data.cellphone || '無';
  document.querySelector('#name-information').textContent = data.name || '';
  document.querySelector('#email-information').textContent = data.email || '';
  document.querySelector('#birthday-information').textContent = data.birthday || '';
  document.querySelector('#callPhone-information').textContent = data.cellphone || '';
}

const idPageStorageButton = document.querySelector('#id_page_storage-button');

idPageStorageButton.addEventListener('click', InputIdPageData);

async function InputIdPageData(){
  try{
    const token = localStorage.getItem('Token');
    let idData  = GetIdDataInInput();
    if (!idData) {
      return null;
    } else{
      const response = await fetchAPI("/api/member/memberDetails", token, 'POST', idData)
      // const response = await inputIdDataToDB();
      const data = await handleResponse(response);
      idEditMessage(data);
    }
  }catch(error){
    handleError(error);
  }
}

function GetIdDataInInput() {
  const name = document.querySelector('#name-information').value;
  const email = document.querySelector('#email-information').value;
  const birthday = document.querySelector('#birthday-information').value;
  const cellphone = document.querySelector('#callPhone-information').value;
  const password = document.querySelector('#password-information').value;
  if (!name && !email && !birthday && !cellphone && !password) {
    const messageContainer = document.querySelector('#id-edit-success-message');
    messageContainer.textContent = '請輸入編輯資料!';
    return null;
  }
  return {
    "name": name,
    "email": email,
    "birthday": birthday,
    "cellphone": cellphone,
    "password": password
  };
}

function idEditMessage(data){
  let messageContainer = document.querySelector('#id-edit-success-message')
  if (data.ok){
      messageContainer.textContent = '編輯成功!';
      window.location.href = '/id'
  }
};