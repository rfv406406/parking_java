// 複數通用函式來切換類
function toggleClass(elementSelector, classToToggle) {
  let elements = document.querySelectorAll(elementSelector);
  if (elements){
    elements.forEach(element => {
      element.classList.toggle(classToToggle);
    });
  }
}

// 事件監聽設置的通用函式
function setupToggle(buttonSelector, toggles) {
  const toggleAll = () => {
    toggles.forEach(function(toggle) {
      toggleClass(toggle.elementSelector, toggle.classToToggle);
    });
  };
  let button = document.querySelector(buttonSelector);
  if (button){
    button.addEventListener('click', (event) => {
      event.preventDefault();
      toggleAll();  
    });
  }
  return toggleAll;
}

// 事件監聽設置的通用函式//排除按鈕
function setupToggleNotButtonElements(element, toggles) {
  let clickElement = document.querySelector(element)
  if (clickElement){
    clickElement.addEventListener('click', function(event) {
      if (!event.target.matches('button') && !event.target.matches('#data-type-selector')) {
        toggles.forEach(function(toggle) {
          toggleClass(toggle.elementSelector, toggle.classToToggle);
        });
      }
    });
  }
}

// 事件監聽設置的通用函式
function removeClass(elementSelector, classesToRemove) {
  let element = document.querySelector(elementSelector);
  if (element) {
      classesToRemove.forEach(cssClass => {
          if (element.classList.contains(cssClass)) {
              element.classList.remove(cssClass);
          }
      });
  }
}

// 事件監聽設置的通用函式 
function setupRemoveButton(Selector, elementSelectorANDcss) {
  let element = document.querySelector(Selector);
  if (element) {
    element.addEventListener('click', function() {
      elementSelectorANDcss.forEach(item => {
        removeClass(item.elementSelector, item.css);
      });
    });
  };
}

// 事件監聽設置的通用函式 //非元件類
function setupRemove(element, elementSelectorANDcss) {
  let button = document.querySelector(element)
  if (button) {
    button.addEventListener('click', () => {
      elementSelectorANDcss.forEach(item => {
        removeClass(item.elementSelector, item.css);
      });
    });
  }
}

//car_page edit
setupToggle('#plate-board-edit-button', [
  { elementSelector: '#plate-board-container-header', classToToggle: 'plate-board-container-header-toggled' }
])

//parking_lot-information-container go up顯示
setupToggleNotButtonElements('.parking_lot-information-container', [
    { elementSelector: '.parking_lot-information-container', classToToggle: 'parking_lot-information-container-toggled' }
])

setupRemove('.map', [
    { elementSelector: '.parking_lot-information-container', 
    css: ['parking_lot-information-container-toggled', 'parking_lot-information-container-appear'] }
])

//parking_page
//parking_page block顯示
let parkingPageButtonList = setupToggle('#parking-page-button-list', [
  { elementSelector: '#packing-page-container', classToToggle: 'packing-page-container-toggled' },
  { elementSelector: '#packing-page-black-back', classToToggle: 'black-back-toggled' }
])

setupRemoveButton('#close-packing-page', [
  { elementSelector: '#packing-page-container', css: ['packing-page-container-toggled'] },
  { elementSelector: '#packing-page-black-back', css: ['black-back-toggled'] },
  { elementSelector: '#menuContent', css: ['menuContent_toggled'] },
  { elementSelector: '#packing-page-information-none', css: ['packing-page-information-none-toggled'] },
  { elementSelector: '#packing-page-car-board-selected', css: ['packing-page-car-board-selected-toggled'] },
  { elementSelector: '.parking_lot-information-container', css: ['parking_lot-information-container-toggled', 'parking_lot-information-container-appear'] }
])

//alert_page none顯示
setupRemoveButton('#alert-content-checked-button', [
    { elementSelector: '#alert-page-container', css: ['alert-page-container-toggled'] },
    { elementSelector: '#alert-page-black-back', css: ['alert-page-black-back-toggled'] },
  ])

//base
//使用函式來設置事件監聽器
setupToggle('#menu', [
  { elementSelector: '#menuContent', classToToggle: 'menuContent_toggled' }
]);

setupToggle('#signin-button-list', [
  { elementSelector: '#signin-container', classToToggle: 'signin-container-toggled' },
  { elementSelector: '#black-back', classToToggle: 'black-back-toggled' }
]);

setupToggle('#go-signon', [
  { elementSelector: '#signin-container', classToToggle: 'signin-container-toggled' },
  { elementSelector: '#signon-container', classToToggle: 'signup-container-toggled' }
]);

setupToggle('#return-signin', [
  { elementSelector: '#signin-container', classToToggle: 'signin-container-toggled' },
  { elementSelector: '#signon-container', classToToggle: 'signup-container-toggled' }
]);

let toggleSignIn = setupToggle('#close-signin', [
  { elementSelector: '#signin-container', classToToggle: 'signin-container-toggled' },
  { elementSelector: '#black-back', classToToggle: 'black-back-toggled' },
  // { elementSelector: '#menuContent', classToToggle: 'menuContent_toggled' }
]);

setupToggle('#close-signon', [
  { elementSelector: '#signon-container', classToToggle: 'signup-container-toggled' },
  { elementSelector: '#black-back', classToToggle: 'black-back-toggled' },
  { elementSelector: '#menuContent', classToToggle: 'menuContent_toggled' }
]);

//id
setupToggle('#id_page_edit-button', [
  { elementSelector: '#information-page', classToToggle: 'information-page-toggled' },
  { elementSelector: '#information-container', classToToggle: 'information-container-toggled' }
]);

setupToggle('#close-information', [
  { elementSelector: '#information-page', classToToggle: 'information-page-toggled' },
  { elementSelector: '#information-container', classToToggle: 'information-container-toggled' }
]);

//parking_lot_page
setupToggle('#parking-lot-page-increase', [
  { elementSelector: '#parking-lot-information-container', classToToggle: 'parking-lot-information-container-toggled' },
  { elementSelector: '#parking-lot-page', classToToggle: 'parking-lot-page-toggled' }
]);

setupToggle('#parking-lot-information-page-close', [
  { elementSelector: '#parking-lot-page', classToToggle: 'parking-lot-page-toggled' },
  { elementSelector: '#parking-lot-information-page', classToToggle: 'parking-lot-information-page-toggled' }
]);

setupToggle('#parking-lot-information-page-edit-button', [
  { elementSelector: '#parking-lot-information-container', classToToggle: 'parking-lot-information-container-toggled' },
  { elementSelector: '#parking-lot-information-page', classToToggle: 'parking-lot-information-page-toggled' }
]);

setupToggle('#parking-lot-square-page-close', [
  { elementSelector: '#parking-lot-square-page', classToToggle: 'parking-lot-square-page-toggled' },
  { elementSelector: '#parking-lot-information-page', classToToggle: 'parking-lot-information-page-toggled' }
]);

setupToggle('#parking-lot-information-container-close', [
  { elementSelector: '#parking-lot-information-container', classToToggle: 'parking-lot-information-container-toggled' },
  { elementSelector: '#parking-lot-page', classToToggle: 'parking-lot-page-toggled' }
]);

function setupToggleEventDelegation(parentSelector, buttonSelector, toggles) {
  let dom = document.querySelector(parentSelector);
  if (dom) {
    dom.addEventListener('click', function(event) {
      if (event.target.matches(buttonSelector)) {
          event.preventDefault();
          toggles.forEach(function(toggle) {
              toggleClass(toggle.elementSelector, toggle.classToToggle);
          });
      }
  });
  }
}

setupToggleEventDelegation('#parking-lot-container', '.parking-lot-information-page-go-button', [
  { elementSelector: '#parking-lot-information-page', classToToggle: 'parking-lot-information-page-toggled' },
  { elementSelector: '#parking-lot-page', classToToggle: 'parking-lot-page-toggled' }
]);

function setupToggleDeleteButton(parentSelector, avoidArea, avoidButton, toggle) {
  let container = document.querySelector(parentSelector);
  if(container) {
    container.addEventListener("click", (event) => {
      event.preventDefault();
      let parkingLotTable = event.target.closest(avoidArea);
      let isGoButton = event.target.closest(avoidButton);
  
      if(parkingLotTable && !isGoButton) {
        if(toggle) {
          let element = parkingLotTable.querySelector(toggle.elementSelector);
          if (element) {
            element.classList.toggle(toggle.classToToggle);
          }
        }
      }
    } )
  }
}

setupToggleDeleteButton('#parking-lot-container', '.parking-lot-page-table', '.parking-lot-information-page-go-button', 
  { elementSelector: '#parking-lot-delete-button', classToToggle: 'parking-lot-delete-button-toggled'}
)

function removeClassOnClickOutside(targetSelector, toggleSelector, classesToRemove) {
  document.addEventListener('click', function(event) {
      let targetElement = document.querySelector(targetSelector);
      let toggleElement = document.querySelector(toggleSelector);
      if (targetElement && !targetElement.contains(event.target) &&
          (!toggleElement || !toggleElement.contains(event.target))) {
          classesToRemove.forEach(cssClass => {
              targetElement.classList.remove(cssClass);
          });
      }
  });
}

removeClassOnClickOutside('#menuContent', '#menu', ['menuContent_toggled']);

function clickButton(buttonSelector, renderTemplate) {
  document.querySelector(buttonSelector).addEventListener('click', async (event) => {
    event.preventDefault();
    if ((buttonSelector == "#parking-page-button-list" && window.location.pathname === "/index") ||
        (buttonSelector == "#parking-page-button-list" && window.location.pathname == "/")) {
      return null;
    } else if (buttonSelector == "#parking-page-button-list" && window.location.pathname !== "/index") {
      await turnPage(renderTemplate, "#parkingPage");
      return null;
    } 
    await turnPage(renderTemplate);
  });
}

async function turnPage(renderTemplate, hash = null){
  const token = tokenChecking();
  let response = await fetchAPI(renderTemplate, token, "GET", data = null);
  if (response.ok && hash == null) {
    window.location.href = renderTemplate;
  } else if (response.ok && hash != null) {
    window.location.href = renderTemplate + hash;
  }
}

clickButton('#parking-lot-button-list','/parkingLotPage');
clickButton('#home','/index')
clickButton('#selector','/selector')
clickButton('#car_page','/carPage');
clickButton('#id','/id');
clickButton('#deposit-and-pay-page-button','/depositPage');
clickButton('#cash-record-page-button','/cashFlowRecord');
clickButton('#parking-page-button-list','/index');
clickButton('#chat-page-button','/chatroom');