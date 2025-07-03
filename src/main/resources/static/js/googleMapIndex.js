//調用停車場DB數據
let parkingLotMap = new Map();
let parkingLotGMPMap = new Map();
let parkingLotGMPArray = [];

async function fetchParkingLotData(currentPosition){
    try{
        const parkingLotParameters = JSON.parse(localStorage.getItem("parkingLotParameters"));
        const response = await getParkingLotMap(currentPosition.lng, currentPosition.lat, parkingLotParameters, null);
        const data = await handleResponse(response);
        await displayParkingLotMarker(data);
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
            if (parkingLotMap.get(Number(parkingLotId)) || parkingLotGMPMap.get(Number(parkingLotId))) {
                continue;
            }
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
        setMarkerClusterer(parkingLotGMPArray);
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
    parkingLotTag.id = parkingLot.parkingLotId;

    parkingLotTag.setAttribute("parkingLotId", parkingLot.parkingLotId);
    parkingLotTag.setAttribute("lat", parkingLot.latitude);
    parkingLotTag.setAttribute("lng", parkingLot.longitude);
    parkingLotTag.style.backgroundColor = setColorByPrice(parkingLot.price);

    foot.id = "markerFoot";
    foot.className = 'parking-lot-tag-foot';
    foot.style.borderTopColor  = setColorByPrice(parkingLot.price);

    parkingLotTag.appendChild(foot);

    priceTag.className = 'priceTag';
    priceTag.id = "priceLabel";
    priceTag.style.textOverflow = "ellipsis";
    
    priceTag.textContent = labelContent;
    parkingLotTag.appendChild(priceTag);

    parkingLotTag.classList.add('parking-lot-tag', 'parking-float');

    parkingLotTag.addEventListener('mouseenter', () => {
        parkingLotTag.classList.add('parking-float--paused');
    });

    parkingLotTag.addEventListener('mouseleave', () => {
        parkingLotTag.classList.remove('parking-float--paused');
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

function setColorByPrice(price) {
    if (price <= 25) {
        return 'rgba(0, 255, 68, 0.6)'; 
    } else if (price <= 50) {
        return 'rgba(255, 145, 0, 0.6)'; 
    } else {
        return 'rgba(255, 25, 0, 0.6)'; 
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
        const searchBar = document.querySelector('.search-bar');
        const blackBackground = document.querySelector('.black-back-background');
        const destination = document.querySelector("#searchInput").value;
        const responseArray = await getSearchingLocation(destination);
        const destinationPosition = {
            "lat" : responseArray[0].location.lat(),
            "lng" : responseArray[0].location.lng()
        }

        await displayMarker(null, responseArray);
        map.setZoom(15);
        if (blackBackground) {
            document.body.removeChild(blackBackground);
            searchBar.style.display = 'none';
        }
        await fetchParkingLotData(destinationPosition);
    } catch(error) {
        console.error(error);
        const alertMessage = '您輸入的地點不存在';
        displayAlertMessage(alertMessage);
    }

})
