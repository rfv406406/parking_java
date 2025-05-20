let map;
let currentInfoWindow;
let currentPosition;
let directionsService;
let directionsRenderer;
let geocoder;
let advancedMarkerElement;
let markerClusterer;
let currentPositionMarker;
let cluster;

async function initMap() {
    try {
        // Request needed libraries.
        //@ts-ignore
        const center = { lat: 23.553118, lng: 121.0211024 };
        const { Map } = await google.maps.importLibrary("maps");
        const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
        advancedMarkerElement = AdvancedMarkerElement;
        const { MarkerClusterer } = await import('https://cdn.skypack.dev/@googlemaps/markerclusterer@2.3.1');
        markerClusterer = MarkerClusterer;

        map = new Map(document.querySelector("#map"), {
            zoom: 7,
            center: center,
            mapId: "Parking_Map",
            gestureHandling: "greedy",
        });
        map.setCenter({ lat: 23.553118, lng: 121.0211024 });

        currentInfoWindow = new google.maps.InfoWindow();
        directionsRenderer = new google.maps.DirectionsRenderer();
        directionsService = new google.maps.DirectionsService();
        geocoder = new google.maps.Geocoder();

        //return center
        currentPosition = await returnCurrentPosition();
        currentPositionMarker = await displayMarker(currentPosition);
        //匯入停車場
        fetchParkingLotData();
    } catch(error) {
        console.error(error);
        const alertContent = document.querySelector("#alert-content")
        alertContent.textContent = error.message;
        toggleClass('#alert-page-container', 'alert-page-container-toggled');
        toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
    }
}

async function calculateAndDisplayRoute(directionsService, directionsRenderer, destination) {
    try {
        let currentPosition = await returnCurrentPosition();
        let query = {
            "origin":{ "lat": currentPosition.lat, "lng":currentPosition.lng },
            "destination":destination,
            travelMode: google.maps.TravelMode.DRIVING
        };
        let response = await directionsService.route(query);
        directionsRenderer.setDirections(response);   
    } catch(error) {
        console.log(error);
        throw error;
    }
  }

// async function reverseGeocoding(geocoder, map, infoWindow, currentPosition) {
//     try {
//         let location = {"location": currentPosition};
//         let response = await geocoder.geocode(location);

//         if (response.results[0]) {
//             return response.results[0];
//         } else {
//             throw new Error("No results found");
//         }
//     } catch(error) {
//         console.error(error);
//         throw error;
//     }
// }

async function returnCurrentPosition() {
    return new Promise((resolve, reject) => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                  const pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude,
                  };
                resolve(pos);  
                },
                (error) => {
                    reject(new Error(error.message));                    
                },
              );
        } else {
            reject(new Error("Browser doesn't support Geolocation"));
        }    
    })
}

async function renderSearchingLocation(destination) {
    try {
        const { Place } = await google.maps.importLibrary("places");
        const request = {
            textQuery: destination,
            fields: ['displayName', 'location', 'businessStatus', 'formattedAddress'],
            isOpenNow: true,
            language: 'zh-TW',
            maxResultCount: 8,
            minRating: 3.2,
            region: 'tw', // 使用 region 限定在台灣
        };

        const { places } = await Place.searchByText(request);
        let responseArray = [];

        if (places.length) {
            const { LatLngBounds } = await google.maps.importLibrary("core");
            const bounds = new LatLngBounds();
            // Loop through and get all the results.
            places.forEach((place) => {
                responseArray.push(place);
                bounds.extend(place.location);
            });
            await displayMarker(null, responseArray);
            map.fitBounds(bounds);
        }
        else {
            console.log('No results');
            throw new Error("No results");
        }
    } catch(error) {
        throw new Error(error.message);
    }
}

let displayMarkerArray = [];

async function displayMarker(currentPosition = null, places = null){
    map.setCenter(currentPosition || places[0].location);
    map.setZoom(15);
    if (places != null) {
        cleanDisplayMarkerArray();
        for (let i=0; i<places.length; i++) {
            const marker = new advancedMarkerElement({
                map: map,
                position: places[i].location,
                title: places[i].displayName,
            });
            let infoWindow = new google.maps.InfoWindow();
            infoWindow.setPosition(places[i].location);
            infoWindow.setContent(`${places[i].displayName}<br>${places[i].formattedAddress}`);
            marker.addListener("click", async () => {
                let destination = {
                    "lat": marker.position.lat,
                    "lng": marker.position.lng
                }
                infoWindow.open(map);
                directionsRenderer.setMap(map);
                await calculateAndDisplayRoute(directionsService, directionsRenderer, destination);
            })
            marker.addEventListener("mouseleave", () => {
                setTimeout(() => {
                    infoWindow.close(map);
                }, 1000);
            })
            displayMarkerArray.push(marker);
        }
    }
    if (currentPosition != null) {
        const carImage = document.createElement("img");
        carImage.src = "/image/carImage.png";
        carImage.style.width = "40px";

        const marker = new advancedMarkerElement({
            map: map,
            position: currentPosition,
            content: carImage,
            title: "目前所在位置",
        });
        currentInfoWindow.setPosition(currentPosition);
        currentInfoWindow.setContent("目前所在位置");
        marker.addEventListener("click", () => {
            currentInfoWindow.open(map);
        })
        return marker;
    }
}

function cleanDisplayMarkerArray() {
    for (let i=0; i<displayMarkerArray.length; i++) {
        displayMarkerArray[i].setMap(null);
        displayMarkerArray[i] = null;
    }
    displayMarkerArray = [];
}

initMap();
// window.initMap = initMap;