initCashRecord();
let cashRecords;

async function initCashRecord(){
    const token = localStorage.getItem('Token');
    try{
        const response = await fetchAPI("/api/transactionRecords", token, 'GET')
        const data = await handleResponse(response);
        cashRecords = data;
        filterAndDisplayRecords(cashRecords);
    }catch(error){
        handleError(error);
    }
}

// async function getCashRecordData(){
//     const token = localStorage.getItem('Token');
//     const response = await fetchAPI("/api/transactionRecords", token, 'GET')
//     return response;
// }

// ---------------------------------------------------------------------------------

document.querySelector('#data-type-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});
document.querySelector('#time-range-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});

function filterAndDisplayRecords(cashRecords) {
    const dataTypeSelector = document.querySelector('#data-type-selector');
    const timeRange = document.querySelector('#time-range-selector').value;

    const dataTypeMapping = {
        'type1': 'DEPOSIT',
        'type2': 'CONSUMPTION',
        'type3': 'INCOME'
    };
    // const dataTypeMapping = {
    //     'type1': 'transactions',
    //     'type2': 'consumption_payment',
    //     'type3': 'consumption_income'
    // };

    let selectedDataType = dataTypeMapping[dataTypeSelector.value];
    // let relevantData = cashRecords[selectedDataType];
    // 確保 relevantData 是為array
    
    // if (!Array.isArray(relevantData)) {
    //     relevantData = [];
    // }
    // console.log(relevantData)
    // filter
    // if (selectedDataType === 'transactions') {
    //     relevantData = relevantData.filter(record => record.Type === 'DEPOSIT');
    // }
    let relevantData = cashRecords.filter(item => item.transactionType === selectedDataType);
    
    // 根據所選時間過濾data
    let filteredRecords = relevantData.filter(item => {
        return matchesTimeRange(item, timeRange);
    });

    // 動態生成
    displayRecords(filteredRecords, selectedDataType);
}

function matchesTimeRange(record, timeRange) {
    let recordDate = new Date(record.transactionsTime);

    // // 確定記錄的類型並解析日期
    // if (record.stoptime) { // 對於 'consumption_income' 和 'consumption_payment'
    //     recordDate = new Date(record.stoptime);
    // } else if (record.transactions_time) { // 對於 'transactions'
    //     recordDate = new Date(record.transactions_time);
    // } else {
    //     return false; 
    // }

    // 根據時間範圍進行比較
    switch (timeRange) {
        case 'this_week':
            return isThisWeek(recordDate);
        case 'this_month':
            return isThisMonth(recordDate);
        case 'all':
            return true; 
        default:
            return false; 
    }
}

function isThisWeek(date) {
    const now = new Date();
    const firstDayOfWeek = new Date(now.setDate(now.getDate() - now.getDay() + (now.getDay() === 0 ? -6 : 1))); // 星期一
    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(lastDayOfWeek.getDate() + 6); // 星期天

    return date >= firstDayOfWeek && date <= lastDayOfWeek;
}

function isThisMonth(date) {
    const now = new Date();
    const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDayOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    return date >= firstDayOfMonth && date <= lastDayOfMonth;
}

function displayRecords(cashRecords, dataType) {
    const container = document.querySelector('#plate-board-information');
    container.textContent  = ''; 

    if (cashRecords.length === 0) {
        let noDataDiv = document.createElement('div');
        noDataDiv.textContent = '沒有交易資料';
        container.appendChild(noDataDiv);
        // container.innerHTML = '<div>沒有交易資料</div>';
        return null;
    }

    cashRecords.forEach(record => {
        // let htmlContent = '';
        switch (dataType) {
            case 'DEPOSIT': {
                let depositDiv = document.createElement('div');
                depositDiv.className = 'plate-board-information';
                depositDiv.id = record.id;
                let depositDate = document.createElement('div');
                depositDate.textContent = `日期: ${record.transactionsTime}`;
                let depositAmount = document.createElement('div');
                depositAmount.textContent = `儲值金額: ${record.amount} 元`;
                depositDiv.appendChild(depositDate);
                depositDiv.appendChild(depositAmount);
                container.appendChild(depositDiv);
                // htmlContent = `<div class="plate-board-information">
                // <div>日期: ${record.transactions_time}</div>
                // <div>儲值金額: ${record.Amount}${'元'}</div>
                // </div>`;
                break;
            }
                
            case 'COMSUMPTION': {
                let comsumptionDiv = document.createElement('div');
                comsumptionDiv.className = 'plate-board-information';
                comsumptionDiv.id = record.id;
                let comsumptionDate = document.createElement('div');
                comsumptionDate.textContent = `日期: ${record.transactionsTime}`;
                let carNumber = document.createElement('div');
                carNumber.textContent = `車牌: ${record.car.carNumber}`;
                let parkingLot = document.createElement('div');
                parkingLot.textContent = `停車場: ${record.parkingLot.name}`;
                let carSpaceNumber = document.createElement('div');
                carSpaceNumber.textContent = `車位編號: ${record.carSpaceNumber.value} 號`;
                let parkingTimeStart = document.createElement('div');
                parkingTimeStart.textContent = `停車時間: ${record.startTime}`;
                let parkingTimeEnd = document.createElement('div');
                parkingTimeEnd.textContent = `結束時間: ${record.stopTime}`;
                let fee = document.createElement('div');
                fee.textContent = `停車費: ${record.amount} 元`;

                const elementsC = [
                    comsumptionDate,
                    carNumber,
                    parkingLot,
                    carSpaceNumber,
                    parkingTimeStart,
                    parkingTimeEnd,
                    fee
                ]
                elementsC.forEach(element => comsumptionDiv.appendChild(element));
                container.appendChild(comsumptionDiv); 
                // htmlContent = `<div class="plate-board-information">
                //                <div>日期: ${record.date}</div>
                //                <div>車牌: ${record.car_board}</div>
                //                <div>停車場: ${record.parkinglotname}</div>
                //                <div>車位編號: ${record.square_number}</div>
                //                <div>停車時間: ${record.starttime}</div>
                //                <div>結束時間: ${record.stoptime}</div>
                //                <div>停車費: ${record.payment}${'元'}</div>
                //                </div>`;
                break;
            }
                
            case 'INCOME': {
                let incomeDiv = document.createElement('div');
                incomeDiv.className = 'plate-board-information';
                incomeDiv.id = record.id;
                let incomeDate = document.createElement('div');
                incomeDate.textContent = `日期: ${record.transactionsTime}`;
                let carNumber = document.createElement('div');
                carNumber.textContent = `車牌: ${record.car.carNumber}`;
                let parkingLot = document.createElement('div');
                parkingLot.textContent = `停車場: ${record.parkingLot.name}`;
                let carSpaceNumber = document.createElement('div');
                carSpaceNumber.textContent = `車位編號: ${record.carSpaceNumber.value} 號`;
                let parkingTimeStart = document.createElement('div');
                parkingTimeStart.textContent = `停車時間: ${record.startTime}`;
                let parkingTimeEnd = document.createElement('div');
                parkingTimeEnd.textContent = `結束時間: ${record.stopTime}`;
                let income = document.createElement('div');
                income.textContent = `收入: ${record.amount} 元`;

                const elementsI = [
                    incomeDate,
                    carNumber,
                    parkingLot,
                    carSpaceNumber,
                    parkingTimeStart,
                    parkingTimeEnd,
                    income
                ]
                elementsI.forEach(element => incomeDiv.appendChild(element));
                container.appendChild(incomeDiv); 
                // htmlContent = `<div class="plate-board-information">
                //                <div>日期: ${record.date}</div>
                //                <div>停車場: ${record.parkinglotname}</div>
                //                <div>車牌編號: ${record.car_board}</div>
                //                <div>停車時間: ${record.starttime}</div>
                //                <div>結束時間: ${record.stoptime}</div>
                //                <div>收入: ${record.income}${'元'}</div>
                //                </div>`;
                break;
            }
        }
        // container.innerHTML += htmlContent;
    });
}