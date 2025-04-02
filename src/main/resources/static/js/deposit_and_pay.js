TPDirect.setupSDK(137033, 'app_g5H5hXkKSIHANVJsYh99hPcebudiWGo3YokDL3zG8kxYMZT4bX4bQoKJbi7V', 'sandbox')
// console.log(TPDirect);

//顯示儲值金額
document.querySelector('#deposit-number').addEventListener('input', function() {
    let inputValue = this.value;
    inputValue = inputValue.replace(/\D/g, '');
    document.querySelector('#total_price').textContent = inputValue;
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

document.querySelector('.pay_button').addEventListener('click', function (event) {
    event.preventDefault();

    const tappayStatus = TPDirect.card.getTappayFieldsStatus();

    if (tappayStatus.canGetPrime === false) {
        alert('請輸入金額及付款資料');
        return null;
    }
    // Get prime
    TPDirect.card.getPrime(function (result) {
        // alert('get prime 成功，prime: ' + result.card.prime);
        depositData(result.card.prime);
    });
});

function depositData(prime) {
    console.log(prime);
    let totalPrice = document.querySelector("#total_price").textContent;

    if (totalPrice=="" || totalPrice==0){
        alert('請輸入金額')
        return null;
    }

    const token = localStorage.getItem('Token');
    const json = {
        "prime": prime,
        "deposit": totalPrice,
    };
    fetch("/api/pay", {
            method: 'POST',
            body: JSON.stringify(json), 
            headers: {
                "Content-type": "application/json",
                'Authorization': `Bearer ${token}`
            }
        })
        .then(handleResponse)
        .then(data => {
            if(data.data.payment.message === "付款成功"){
                const paySuccess = document.querySelector('#pay-success');
                paySuccess.textContent = '感謝您的加值!'
                // setTimeout(function() {
                //     window.location.href = '/';
                // }, 2000);
            }else{
                alert('付款失敗')
            }
        })
        .catch(
            handleError
        )
    };
