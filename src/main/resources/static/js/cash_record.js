initCashRecord();
let cashRecords = [];

async function initCashRecord(){
    const token = tokenChecking();
    try{
        const response = await fetchAPI("/api/transactionRecords", token, 'GET')
        const data = await handleResponse(response);
        cashRecords = data;
        filterAndDisplayRecords(cashRecords);
    }catch(error){
        handleError(error);
    }
}
// ---------------------------------------------------------------------------------

document.querySelector('#data-type-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});
document.querySelector('#time-range-selector').addEventListener('change', () => {
    filterAndDisplayRecords(cashRecords);});

function filterAndDisplayRecords(cashRecords) {
    const cashRecordType = document.querySelector('#data-type-selector').value;
    const timeRange = document.querySelector('#time-range-selector').value;
    if (cashRecords == null) {
        displayRecordsIsNull();
        return null;
    }

    let relevantData = cashRecords.filter(item => item.transactionType === cashRecordType);
    
    let filteredRecords = relevantData.filter(item => {
        return matchesTimeRange(item, timeRange);
    });

    displayRecords(filteredRecords, cashRecordType);
}

function matchesTimeRange(record, timeRange) {
    let recordDate = new Date(record.transactionsTime);

    switch (timeRange) {
        case 'this_week':
            return inSevenDays(recordDate);
        case 'this_month':
            return inThirtyDays(recordDate);
        case 'all':
            return true; 
        default:
            return false; 
    }
}

function inSevenDays(recordDate) {
    const launchDate = new Date(recordDate);
    const launchDateInMillis = launchDate.getTime();
    const sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000;
    const launchDateAddsevenDays = launchDateInMillis + sevenDaysInMillis;
    const now = new Date();
    const nowInMillis = now.getTime();

    if (launchDateAddsevenDays - nowInMillis > 0 && nowInMillis > launchDate) {
        return true;
    } else {
        return false;
    }
}

function inThirtyDays(recordDate) {
    const launchDate = new Date(recordDate);
    const launchDateInMillis = launchDate.getTime();
    const thirtyDaysInMillis = 30 * 24 * 60 * 60 * 1000;
    const launchDateAddThirtyDays = launchDateInMillis + thirtyDaysInMillis;
    const now = new Date();
    const nowInMillis = now.getTime();

    if (launchDateAddThirtyDays - nowInMillis > 0 && nowInMillis > launchDate) {
        return true;
    } else {
        return false;
    }
}

function displayRecordsIsNull() {
    const container = document.querySelector('#plate-board-information');
    container.textContent  = ''; 
    let noDataDiv = document.createElement('div');
    noDataDiv.textContent = '沒有交易資料';
    container.appendChild(noDataDiv);
}

function displayRecords(cashRecords, dataType) {
    const container = document.querySelector('#plate-board-information');
    container.textContent  = ''; 

    if (cashRecords.length === 0) {
        displayRecordsIsNull();
        return null;
    }

    cashRecords.forEach(record => {
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
                break;
            }
                
            case 'CONSUMPTION': {
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
                carSpaceNumber.textContent = `車位編號: ${record.parkingLot.carSpaceNumber[0].value} 號`;
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
                carSpaceNumber.textContent = `車位編號: ${record.parkingLot.carSpaceNumber[0].value} 號`;
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
                break;
            }
        }
    });
}