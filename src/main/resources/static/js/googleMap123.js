let map;
let currentPosition; 
let selectedPlace;
let marker;
let directionsService;
let directionsRenderer;
let infoWindow;
let newZoom;

async function initMap() {
    //@ts-ignore
    let { Map } = await google.maps.importLibrary("maps");

    map = new Map(document.querySelector('#map'), {
        center: {lat: 23.553118, lng: 121.0211024},
        zoom: 7,
        heading: 90,
        gestureHandling: 'greedy'
        // mapTypeControl: false
    });
    map.addListener("zoom_changed", function() {
        newZoom = map.getZoom();
        console.log("目前縮放等級是：", newZoom);
    });
    navigator.geolocation.getCurrentPosition(async function(position){
        currentPosition = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
        };
        map.setCenter(currentPosition);
        map.setZoom(16);
        await setupAutocompleteListener(map, currentPosition);
    });
    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);
    startTrackingUserPosition();

    document.querySelector('#navigation').addEventListener('click', returnToCurrentPositionAndZoomIn);
};

async function setupAutocompleteListener(map, currentPosition) {
    let autocomplete = await initializeAutocomplete(currentPosition);
    autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();
        selectedPlace = {
            location: place.geometry.location,
            placeId: place.place_id,
            name: place.name,
            address: place.formatted_address,
            rating: place.rating,
            phoneNumber: place.formatted_phone_number
        };
        map.setCenter(selectedPlace.location);

        if (!marker) {
            marker = new google.maps.Marker({ map: map });
        };
        marker.setPosition(selectedPlace.location);

        if (!directionsService) {
            directionsService = new google.maps.DirectionsService();
        };
        if (!directionsRenderer) {
            directionsRenderer = new google.maps.DirectionsRenderer({ map: map });
        };
        directionsRenderer.set('directions', null);

        directionsService.route({
            origin: new google.maps.LatLng(currentPosition.lat, currentPosition.lng),
            destination: { placeId: selectedPlace.placeId },
            travelMode: 'DRIVING',
        }, onRouteCalculated);
    });
}

async function initializeAutocomplete(currentPosition) {
    const input = document.querySelector('#searchInput');
    const autocomplete = new google.maps.places.Autocomplete(input, {
        bounds: {
            east: currentPosition.lng + 0.001,
            west: currentPosition.lng - 0.001,
            south: currentPosition.lat - 0.001,
            north: currentPosition.lat + 0.001,
        },
        strictBounds: false,
    });

    return autocomplete;
};

function returnToCurrentPositionAndZoomIn() {
    if (currentPosition) {
        map.setCenter(currentPosition);
        map.setZoom(20);
        removeClass('.parking_lot-information-container',['parking_lot-information-container-toggled', 'parking_lot-information-container-appear'])
    }
}

function startTrackingUserPosition() {
    navigator.geolocation.watchPosition(function(position) {
        currentPosition = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
        };
        console.log("目前位置: ", currentPosition);
        // 如果marker已经存在，更新它的位置
        if (!marker) {
            marker = new google.maps.Marker({
                position: currentPosition,
                map: map,
                title: "您的位置"
            });
        } else {
            marker.setPosition(currentPosition);
        }
    }, function(error) {
        console.error("Error watching position: " + error.code);
        console.error("Error watching position: " + error.message);
    }, {
        enableHighAccuracy: true,
        maximumAge: 1000000000, // 每秒更新位置
        timeout: 30000
    });
}

function onRouteCalculated(response, status) {
    if (status === 'OK') {
        directionsRenderer.setDirections(response);
        if (!infoWindow) {
            infoWindow = new google.maps.InfoWindow();
        }
        infoWindow.setContent(
            `<h3>${selectedPlace.name}</h3>
            <div>地址: ${selectedPlace.address}</div>
            <div>電話: ${selectedPlace.phoneNumber}</div>
            <div>評分: ${selectedPlace.rating}</div>
            <div>開車時間: ${response.routes[0].legs[0].duration.text}</div>`
        );
        infoWindow.open(map, marker);
    };
}

// 最短路徑
async function calculateAndDisplayRoute(directionsService, directionsRenderer, origin, destination) {
    directionsRenderer.setOptions({preserveViewport: true});
    return new Promise((resolve, reject) => {
        directionsService.route({
            origin: { lat: origin.lat, lng: origin.lng },
            destination: { lat: parseFloat(destination.lat), lng: parseFloat(destination.lng) },
            travelMode: 'DRIVING'
        }, (response, status) => {
            if (status === 'OK') {
                directionsRenderer.setDirections(response);
                resolve(response); 
            } else {
                window.alert('Directions request failed due to ' + status);
                reject(status); 
            };
        });
    });
};