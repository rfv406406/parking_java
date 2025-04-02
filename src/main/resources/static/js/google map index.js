fetchData()
//調用停車場DB數據
async function fetchData(){
    try{
        const response = await fetchAPI("/api/input_parking_lot_information", null, 'GET');
        const data = await handleResponse(response);
        await displayMarkers(data);
    }catch(error){
        handleError(error);
    }
}

// ----------------------------------------------------------------------
let markers = []; 
//顯示marker
async function displayMarkers(data) {
    markers.forEach(marker => marker.setMap(null)); //用於將標記從地圖上移除
    markers = [];
    console.log(data)
    if (data) {
        data.data.forEach(location => {
            const marker = createMarker(location);
            // const infoWindow = createInfoWindow(location);
        
            marker.addListener('click', async function() {
                const locationData = findDataByLatLng(location.lat, location.lng, data.data);
                await calculateAndDisplayRoute(directionsService, directionsRenderer, currentPosition, locationData);
                setupAppear([{ elementSelector: '.parking_lot-information-container', classToToggle: 'parking_lot-information-container-appear'}]);
                // infoWindow.open(map, marker);
                parkingLotInformationTable(locationData);
                getBookingInformation(locationData);
            });
            markers.push(marker);
        });
    }
};

//產稱自定義marker
function createMarker(location) {
    let labelContent = '';
    const latLng = {lat: parseFloat(location.lat), lng: parseFloat(location.lng)};
    // 檢查是否存在停車空間和第一個空間的狀態
    if (location.squares && location.squares.every(square => square.status !== '閒置中')) {
        labelContent = "使用中";
    }else{
        labelContent = location.price + "元";
    }
    const marker = new markerWithLabel.MarkerWithLabel({
        position: latLng,
        clickable: true,
        draggable: false,
        map: map,
        labelContent: labelContent,
        labelAnchor: new google.maps.Point(-22, -44),
        labelClass: "labels",
        labelStyle: { opacity: 1.0 },
        icon: {
            url: getIconUrl(location.price),
            scaledSize: new google.maps.Size(60, 50),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(25, 50)
        }
    });
    return marker;
}

//依照價錢分顏色
function getIconUrl(price) {
    if (price <= 25) {
        return '../static/IMAGE/greenlable.png'; 
    } else if (price <= 50) {
        return '../static/IMAGE/orangelable.png'; 
    } else {
        return '../static/IMAGE/redlable.png'; 
    }
}

function findDataByLatLng(lat, lng, data) {
    // 在 data 中查找與給定經緯度匹配的條目
    // print(data)
    return data.find(item => item.lat === lat && item.lng === lng);
};

//返回中心點
document.querySelector('#returnToCurrentPosition').addEventListener('click', function() {
    if (currentPosition) {
        map.setCenter(currentPosition);
        map.setZoom(15);
    } else {
        console.log('當前位置未知');
    }
});

document.querySelector('#search-goal-button').addEventListener('click',openSearchBar);

function openSearchBar(){
    let blackBackBackground = document.querySelector('.black-back-background');
    let searchBar = document.querySelector('.search-bar');
  
    if (!blackBackBackground) {
        blackBackBackground = document.createElement('div');
        blackBackBackground.classList.add('black-back-background');
        blackBackBackground = document.createElement('div');
        blackBackBackground.classList.add('black-back');
        blackBackBackground.style.position = 'fixed';
        blackBackBackground.style.top = '0';
        blackBackBackground.style.left = '0';
        blackBackBackground.style.width = '100%';
        blackBackBackground.style.height = '100%';
        blackBackBackground.style.backgroundColor = 'rgba(0, 0, 0, 0.25)';
        blackBackBackground.style.zIndex = '100';
        blackBackBackground.style.display = 'block';
        document.body.appendChild(blackBackBackground);
        if (searchBar) {
          searchBar.style.display = 'block';
      }
        // 為新創建的 blackBackBackground 添加點擊事件監聽器
        blackBackBackground.addEventListener('click', function() {
            document.body.removeChild(blackBackBackground);
            if (searchBar) {
                searchBar.style.display = 'none';
            }
        });
    }
    
  }

document.addEventListener('DOMContentLoaded', function() {
    // 檢查是否為首次訪問
    if (!sessionStorage.getItem('firstVisitExecuted')) {
        // 執行您希望在首次訪問時執行的操作
        openSearchBar();
        // 設置 session 標記，以便下次不再執行
        sessionStorage.setItem('firstVisitExecuted', 'true');
    }
});