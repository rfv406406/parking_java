//調用停車場DB數據
let parkingLotMap = new Map();
let parkingLotGMPMap = new Map();
let parkingLotGMPArray = [];

async function fetchParkingLotData(){
    try{
        const response = await fetchAPI("/api/parkingLot", null, 'GET');
        const data = await handleResponse(response);
        await displayParkingLotMarker(data);
        setMarkerClusterer(parkingLotGMPArray);
    }catch(error){
        handleError(error);
    }
}

function setMarkerClusterer(parkingLotGMPArray) {
    cluster = new markerClusterer({
            map,
            markers: parkingLotGMPArray
          });     
}

async function displayParkingLotMarker(data) {
    if (data) {
        for (const parkingLotId in data) {
            const parkingLot = data[parkingLotId];
            const parkingLotMarker = createParkingLotMarker(parkingLot);
            const parkingLotMarkerDOM = parkingLotMarker.parkingLotDOM;
            const parkingLotMarkerGMP = parkingLotMarker.parkingLotMarker;
            parkingLotGMPArray.push(parkingLotMarkerGMP);
            parkingLotMap.set(parkingLot.parkingLotId, parkingLot);
            parkingLotGMPMap.set(parkingLot.parkingLotId, parkingLotMarkerGMP);
            parkingLotMarkerDOM.addEventListener("click", async (event) => {
                event.stopPropagation();
                let parkingLotInforTable = document.querySelector("#parking-lot-information-container");
                parkingLotInforTable.style.display = "block";
                const parkingLotIdStr = event.currentTarget.getAttribute("parkingLotId");
                const parkingLot = parkingLotMap.get(Number(parkingLotIdStr));        
                parkingLotInformationTable(parkingLot);
                await navigation(parkingLot);
            });
        }
    }
};

//關閉parkinglotTable
document.querySelector("#map").addEventListener("click", () => {
    document.querySelector("#parking-lot-information-container").style.display = "none";
})

function setParkingLotMarkerLabel(parkingLot) {
    let labelContent = "";
    if (parkingLot.carSpaceNumber && parkingLot.carSpaceNumber.every(square => square.status !== '閒置中')) {
        labelContent = "使用中";
    }else{
        labelContent = parkingLot.price + "元";
    }

    return labelContent;
}

function createParkingLotMarker(parkingLot) {
    const labelContent = setParkingLotMarkerLabel(parkingLot);
    const parkingLotTag = document.createElement("div");
    const priceTag = document.createElement("div");
    const foot = document.createElement("div");
    
    parkingLotTag.id = `parkingLotTag${parkingLot.parkingLotId}`;
    console.log("parkingLotTag.id: " + parkingLotTag.id)
    parkingLotTag.setAttribute("parkingLotId", parkingLot.parkingLotId);
    parkingLotTag.setAttribute("lat", parkingLot.latitude);
    parkingLotTag.setAttribute("lng", parkingLot.longitude);

    parkingLotTag.style.display = "flex";
    parkingLotTag.style.justifyContent = "center"; 
    parkingLotTag.style.alignItems = "center";
    parkingLotTag.style.backgroundColor = setColorByPrice(parkingLot.price);
    parkingLotTag.style.height = "40px";
    parkingLotTag.style.padding = "0px 5px";    
    parkingLotTag.style.borderRadius = "7px";
    parkingLotTag.style.position = "relative";

    foot.id = "markerFoot";
    foot.style.position = "absolute";
    foot.style.left = "50%";
    foot.style.top = "100%";
    foot.style.transform = "translate(-50%, 0)";
    foot.style.width = "0";
    foot.style.height = "0";
    foot.style.borderLeft = "8px solid transparent";
    foot.style.borderRight = "8px solid transparent";
    foot.style.borderTop = "8px solid " + setColorByPrice(parkingLot.price);

    parkingLotTag.appendChild(foot);

    priceTag.id = "priceLabel";
    priceTag.style.display = "flex";
    priceTag.style.justifyContent = "center"; 
    priceTag.style.alignItems = "center";
    priceTag.style.backgroundColor = "rgba(255, 255, 255, 0.5)";
    priceTag.style.boxSizing = "border-box";
    // priceTag.style.width = "80%";
    priceTag.style.minWidth = "40px";
    priceTag.style.height = "80%";
    priceTag.style.padding = "5px";
    priceTag.style.overflow = "hidden";
    priceTag.style.borderRadius = "3px";
    priceTag.style.fontSize = "15px";
    priceTag.style.fontWeight = "bold";
    priceTag.style.color = "rgba(0, 0, 0, 0.63)"
    // priceTag.style.whiteSpace = "nowrap";
     
    priceTag.style.textOverflow = "ellipsis";
    
    priceTag.textContent = labelContent;
    parkingLotTag.appendChild(priceTag);

    //建立 style 標籤，定義 keyframes 動畫
    const style = document.createElement('style');
    style.textContent = `
    @keyframes float {
        0% { transform: translateY(0); }
        50% { transform: translateY(10px); }
        100% { transform: translateY(0); }
    }
    `;
    //無限CSS、逐漸加速進入特效、逐漸緩出特效
    document.head.appendChild(style);
    parkingLotTag.style.animation = "float 2s infinite ease-in-out";

    parkingLotTag.addEventListener('mouseenter', () => {
        parkingLotTag.style.animation = 'none';
    });

    parkingLotTag.addEventListener('mouseleave', () => {
        parkingLotTag.style.animation = 'float 2s infinite ease-in-out';
    });

    const parkingLotMarker = new advancedMarkerElement({
        map,
        position: { lat: parkingLot.latitude, lng: parkingLot.longitude },
        content: parkingLotTag,
        title: parkingLot.name
    });

    let parkingLotObject = {
        "parkingLotDOM": parkingLotTag,
        "parkingLotMarker": parkingLotMarker
    }
    return parkingLotObject;
}

//依照價錢分顏色
function setColorByPrice(price) {
    if (price <= 25) {
        return 'rgba(0, 255, 68, 0.6)'; // 綠色
    } else if (price <= 50) {
        return 'rgba(255, 145, 0, 0.6)'; // 橘色
    } else {
        return 'rgba(255, 25, 0, 0.6)'; // 紅色
    }
}

//return current position
document.querySelector("#returnToCurrentPosition").addEventListener("click", async () => {
    let currentPosition = await returnCurrentPosition();
    await displayMarker(currentPosition);
});

document.querySelector('#search-goal-button').addEventListener('click',openSearchBar);

function openSearchBar(){
    let searchBar = document.querySelector('.search-bar');
    let blackBackground = document.querySelector('.black-back-background');
    searchBar.style.display = 'flex';

    if (!blackBackground) {
        let blackBackground = document.createElement('div');
        blackBackground.classList.add('black-back-background');
        blackBackground.style.position = 'fixed';
        blackBackground.style.top = '0';
        blackBackground.style.left = '0';
        blackBackground.style.width = '100%';
        blackBackground.style.height = '100%';
        blackBackground.style.backgroundColor = 'rgba(0, 0, 0, 0.25)';
        blackBackground.style.zIndex = '100';
        blackBackground.style.display = 'block';
        document.body.appendChild(blackBackground);

        blackBackground.addEventListener('click', () => {
            document.body.removeChild(blackBackground);
            searchBar.style.display = 'none';
            document.querySelector("#searchInput").value = "";
        });  
    } 
}

document.querySelector("#search-bar-button").addEventListener("click", async () => {
    try {
        let searchBar = document.querySelector('.search-bar');
        let blackBackground = document.querySelector('.black-back-background');
        let destination = document.querySelector("#searchInput").value;
        let response = await renderSearchingLocation(destination);
        map.setZoom(15);
        if (blackBackground) {
            document.body.removeChild(blackBackground);
            searchBar.style.display = 'none';
        }
    } catch(error) {
        console.error(error);
        const alertContent = document.querySelector("#alert-content")
        alertContent.textContent = '您輸入的地點不存在';
        toggleClass('#alert-page-container', 'alert-page-container-toggled');
        toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
    }

})
