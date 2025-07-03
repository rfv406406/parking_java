from flask import Blueprint,jsonify,request
from mysql.connector import pooling
from pathlib import Path
from werkzeug.utils import secure_filename
from pyproj import Transformer
import os, time, re, csv, io, random, json
from dotenv import load_dotenv, find_dotenv
load_dotenv(find_dotenv())


config = {
    "host":os.getenv('HOST'),
    "user":os.getenv('USER'),
    "password":os.getenv('PASSWORD'),
    "database":os.getenv('DATABASE'),
    # "host":"db-stage3-week1.cxzjwrl3yccb.us-east-1.rds.amazonaws.com",
    # "port":3306,
    # "user":"root",
    # "password":"12345678",
    # "database":"Stage3",
}
con = pooling.MySQLConnectionPool(pool_name = "mypool",
                              pool_size = 20,
                              **config)

transformer = Transformer.from_crs("EPSG:3826", "EPSG:4326", always_xy=True)

p = Path()
file_path = []

for f in p.glob('response_*.json'):
    file_path.append(f)

file_path.extend(p.glob('TCMSV_alldesc.json'))
file_path.extend(p.glob('新北市路外公共停車場資訊.json'))

price = 0
priceArray = []
for i in range(0, 200):
    price += 5
    priceArray.append(price)

base = Path(__file__).resolve().parent.parent
static_dir = base / 'src' / 'main' / 'resources' / 'static' 
img_dir = static_dir / 'image'

imgArray = []
for img in img_dir.glob('parkingLot*.jpg'):
    rel = img.relative_to(static_dir)
    imgArray.append(str(rel))
    
connection = con.get_connection() 
cursor = connection.cursor()

try:
    for p in file_path:
        with open(p, 'r', encoding='utf-8') as file:
            data = json.load(file)
            dataTopic = ""

            if 'TCMSV_alldesc' in file.name:
                dataTopic = data['data']["park"]
            elif 'response' in file.name:
                dataTopic = data["CarParks"]
            else :
                dataTopic = data
            
            for park in dataTopic:
                member_id = 1
                
                if file.name == 'TCMSV_alldesc.json':
                    name = park['name']
                    if park['EntranceCoord']:
                        lat = float(park['EntranceCoord']['EntrancecoordInfo'][0]['Xcod'])
                        lng = float(park["EntranceCoord"]["EntrancecoordInfo"][0]['Ycod'])
                    else:
                        continue
                    address = park["address"]
                elif 'response' in file.name:
                    name = park['CarParkName']["Zh_tw"]
                    lat = float(park["CarParkPosition"]["PositionLat"])
                    lng = float(park["CarParkPosition"]["PositionLon"])
                    address = park["Address"]
                else:
                    name = park['NAME']
                    address = park["ADDRESS"]
                    lng, lat = transformer.transform(park['TW97X'], park['TW97Y'])

                open_time = random.choice(["00:00", "08:00", "12:00"])
                close_time = "24:00"
                space = random.choice(["室內", "室外"])
                price = random.choice(priceArray)
                width = random.choice([1,2,3,4,5,6])
                height = random.choice([1,2,3])
                

                if not address or lat > 30 or lng < 115:
                    continue

                sql = """
                    INSERT INTO parkinglotdata (member_id, name, address, opening_time, closing_time, space_in_out, price, width_limit, height_limit, lng, lat) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    """
                
                cursor.execute(sql, (member_id, name, address, open_time, close_time, space, price, width, height, lng, lat))

                parkinglot_id = cursor.lastrowid

                for i in range(1, random.randint(2, 6)):
                    sql = """
                        INSERT INTO parkinglotimage (parkinglot_id, image)
                        VALUES (%s, %s);
                    """
                    cursor.execute(sql, (parkinglot_id, random.choice(imgArray)))

                for i in range(1, random.randint(2, 6)):
                    insert_query = """
                        INSERT INTO parkinglotsquare (parkinglot_id, square_number, status)
                        VALUES (%s, %s, '閒置中');
                    """
                    cursor.execute(insert_query, (parkinglot_id, i))

        connection.commit()
except Exception as e:
    print(e)
    raise e
finally:
    cursor.close()
    connection.close()



