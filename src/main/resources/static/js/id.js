fetchIdPageData();
async function fetchIdPageData(){
    try{
      const token = tokenChecking();
      const response = await fetchAPI("/api/member/memberDetails", token, 'GET');
      const data = await handleResponse(response);
      renderIdDataInTableAndInput(data);
    }catch(error){
      handleError(error);
    }
}

function renderIdDataInTableAndInput(data){
  document.querySelector('#id_page_id_number').textContent = data.id || '無';
  document.querySelector('#id_page_account').textContent = data.account || '無';
  document.querySelector('#id_page_name').textContent = data.name || '無';
  document.querySelector('#id_page_e-mail').textContent = data.email || '無';
  document.querySelector('#id_page_birthday').textContent = data.birthday || '無';
  document.querySelector('#id_page_phone').textContent = data.cellphone || '無';
  document.querySelector('#id-information').textContent = data.id || '';
  document.querySelector('#name-information').value = data.name || '';
  document.querySelector('#email-information').value = data.email || '';
  document.querySelector('#birthday-information').value = data.birthday || '';
  document.querySelector('#callPhone-information').value = data.cellphone || '';
}

const idPageStorageButton = document.querySelector('#id_page_storage-button');

idPageStorageButton.addEventListener('click', inputEditData);

async function inputEditData(){
  try{
    const token = tokenChecking();
    let idData  = getEditData();
    if (!idData) {
      return null;
    } else{
      const response = await fetchAPI(`/api/member/memberDetails/${idData.id}`, token, 'PUT', idData)
      const data = await handleResponse(response);
      idEditMessage(data);
    }
  }catch(error){
    handleError(error);
  }
}

function getEditData() {
  const id = document.querySelector('#id-information').textContent;
  const name = document.querySelector('#name-information').value;
  const email = document.querySelector('#email-information').value;
  const birthday = document.querySelector('#birthday-information').value;
  const cellphone = document.querySelector('#callPhone-information').value;
  // const password = document.querySelector('#password-information').value;
  if (!name && !email && !birthday && !cellphone) {
    const messageContainer = document.querySelector('#id-edit-success-message');
    messageContainer.textContent = '請輸入編輯資料!';
    return null;
  }

  let editData = {
    "id":id,
    "name": name,
    "email": email,
    "birthday": birthday,
    "cellphone": cellphone
    // "password": password
  };

  return editData;
}

function idEditMessage(data){
  let messageContainer = document.querySelector('#id-edit-message')
  if (data == "OK"){
      messageContainer.textContent = '編輯成功!';
      setTimeout(() => {window.location.href = '/id'}, 500);
  }
};