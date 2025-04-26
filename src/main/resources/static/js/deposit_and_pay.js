TPDirect.setupSDK(137033, 'app_g5H5hXkKSIHANVJsYh99hPcebudiWGo3YokDL3zG8kxYMZT4bX4bQoKJbi7V', 'sandbox')

//顯示儲值金額
let depositNumber = document.querySelector('#deposit-number');
let totalPrice = document.querySelector('#total_price');
depositNumber.addEventListener('input', () => {
    let inputValue = depositNumber.value;

    let cleanValue = inputValue.replace(/\D/g, '').replace(/^0+/, '');
    depositNumber.value = cleanValue;
    totalPrice.textContent = cleanValue;
});

let fields = {
    number: {
        // css selector
        element: document.querySelector('#card-number'),
        placeholder: '**** **** **** ****'
    },
    expirationDate: {
        // DOM object
        element: document.querySelector('#card-expiration-date'),
        placeholder: 'MM / YY'
    },
    ccv: {
        element: document.querySelector('#card-ccv'),
        placeholder: 'CCV'
    }
};

TPDirect.card.setup({
    styles: {
        // Style all elements
        'input': {
            'color': 'gray',
        },
        // Styling ccv field
        'input.ccv': {
            'font-size': '16px'
        },
        // Styling expiration-date field
        'input.expiration-date': {
            'font-size': '16px'
        },
        // Styling card-number field
        'input.card-number': {
            'font-size': '16px'
        },
        // style focus state
        ':focus': {
            // 'color': 'black'
        },
        // style valid state
        '.valid': {
            'color': 'green'
        },
        // style invalid state
        '.invalid': {
            'color': 'red'
        },
        // Media queries
        // Note that these apply to the iframe, not the root window.
        '@media screen and (max-width: 400px)': {
            'input': {
                'color': 'orange'
            }
        }
    },
    fields: fields,
    isMaskCreditCardNumber: true,
    maskCreditCardNumberRange: {
        beginIndex: 6, 
        endIndex: 11
    }
});

document.querySelector('.pay_button').addEventListener('click', (event) => {
    event.preventDefault();

    const tappayStatus = TPDirect.card.getTappayFieldsStatus();

    if (tappayStatus.canGetPrime === false) {
        alert('請輸入金額及付款資料');
        return null;
    }
    // Get prime
    TPDirect.card.getPrime((result) => {
        depositData(result.card.prime);
    });
});

async function depositData(prime) {
    const token = tokenChecking();
    let totalPrice = document.querySelector("#total_price").textContent;

    if (totalPrice == '') {
        alert('請輸入金額')
        return null;
    }

    const depositDataToJson = {
        "prime":prime,
        "deposit":totalPrice
    }

    try {
        let response = await fetchAPI("api/tappay", token, "POST", depositDataToJson);
        let data = await handleResponse(response);
        if (data === 'Success') {
            const paySuccess = document.querySelector('#pay-success');
            paySuccess.textContent = '感謝您的加值!'
            setTimeout(function() {
                window.location.href = '/';
            }, 2000);
        } else {
            alert('付款失敗');
        }
    } catch(error) {
        handleError(error);
    }    
};