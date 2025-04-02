initCashRecord();
let cashRecords;

async function initCashRecord(){
    try{
        const response = await getCashRecordData();
        const data = await handleResponse(response);
        cashRecords = data;
        filterAndDisplayRecords(cashRecords)
    }catch(error){
        handleError(error);
    }
}

async function getCashRecordData(){
    const token = localStorage.getItem('Token');
    const response = await fetchAPI("/api/cash_record", token, 'GET')
    return response;
}

// ---------------------------------------------------------------------------------

document.querySelector('#data-type-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});
document.querySelector('#time-range-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});

function filterAndDisplayRecords(data) {
    const dataTypeSelector = document.querySelector('#data-type-selector');
    const timeRange = document.querySelector('#time-range-selector').value;

    // 將 select 選項的值映射到對應的鍵
    const dataTypeMapping = {
        'type1': 'transactions',
        'type2': 'consumption_payment',
        'type3': 'consumption_income'
    };

    let selectedDataType = dataTypeMapping[dataTypeSelector.value];
    let relevantData = data[selectedDataType];
    // 確保 relevantData 是為array
    
    if (!Array.isArray(relevantData)) {
        relevantData = [];
    }
    console.log(relevantData)
    // filter
    if (selectedDataType === 'transactions') {
        relevantData = relevantData.filter(record => record.Type === 'DEPOSIT');
    }
    
    // 根據所選時間過濾data
    let filteredRecords = relevantData.filter(record => {
        return matchesTimeRange(record, timeRange);
    });

    // 動態生成
    displayRecords(filteredRecords, selectedDataType);
}

function matchesTimeRange(record, timeRange) {
    let recordDate;

    // 確定記錄的類型並解析日期
    if (record.stoptime) { // 對於 'consumption_income' 和 'consumption_payment'
        recordDate = new Date(record.stoptime);
    } else if (record.transactions_time) { // 對於 'transactions'
        recordDate = new Date(record.transactions_time);
    } else {
        return false; 
    }

    // 根據時間範圍進行比較
    switch (timeRange) {
        case 'this_week':
            return isThisWeek(recordDate);
        case 'this_month':
            return isThisMonth(recordDate);
        case 'all':
            return true; // 對於“全部”，始終返回 true
        default:
            return false; // 如果沒有匹配的時間範圍，返回 false
    }
}

function isThisWeek(date) {
    const now = new Date();
    const firstDayOfWeek = new Date(now.setDate(now.getDate() - now.getDay() + (now.getDay() === 0 ? -6 : 1))); // 這週的第一天（星期一）
    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(lastDayOfWeek.getDate() + 6); // 這週的最後一天（星期天）

    return date >= firstDayOfWeek && date <= lastDayOfWeek;
}

function isThisMonth(date) {
    const now = new Date();
    const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDayOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    return date >= firstDayOfMonth && date <= lastDayOfMonth;
}

function displayRecords(records, dataType) {
    const container = document.querySelector('#plate-board-information');
    container.innerHTML = ''; 

    if (records.length === 0) {
        container.innerHTML = '<div>沒有資料</div>';
        return null;
    }

    records.forEach(record => {
        let htmlContent = '';
        switch (dataType) {
            case 'transactions':
                // 假設 transactions 類型的記錄需要顯示 Amount 和 Type
                htmlContent = `<div class="plate-board-information">
                <div>日期: ${record.transactions_time}</div>
                <div>儲值金額: ${record.Amount}${'元'}</div>
                </div>`;
                break;
            case 'consumption_payment':
                // 假設 consumption_payment 類型的記錄需要顯示不同的字段
                htmlContent = `<div class="plate-board-information">
                               <div>日期: ${record.date}</div>
                               <div>車牌: ${record.car_board}</div>
                               <div>停車場: ${record.parkinglotname}</div>
                               <div>車位編號: ${record.square_number}</div>
                               <div>停車時間: ${record.starttime}</div>
                               <div>結束時間: ${record.stoptime}</div>
                               <div>停車費: ${record.payment}${'元'}</div>
                               </div>`;
                break;
            case 'consumption_income':
                // 假設 consumption_income 類型的記錄顯示又不同的字段
                htmlContent = `<div class="plate-board-information">
                               <div>日期: ${record.date}</div>
                               <div>停車場: ${record.parkinglotname}</div>
                               <div>車牌編號: ${record.car_board}</div>
                               <div>停車時間: ${record.starttime}</div>
                               <div>結束時間: ${record.stoptime}</div>
                               <div>收入: ${record.income}${'元'}</div>
                               </div>`;
                break;
        }
        container.innerHTML += htmlContent;
    });
}