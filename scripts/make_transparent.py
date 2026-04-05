from PIL import Image
import os

input_path = "app/src/main/res/drawable/partition.png"
output_path = "app/src/main/res/drawable/ic_partition_transparent.png"

if os.path.exists(input_path):
    img = Image.open(input_path)
    img = img.convert("RGBA")
    
    datas = img.getdata()
    newData = []
    
    for item in datas:
        # Check if the pixel is close to white
        # If RGB values are high (e.g. > 230), make it transparent
        if item[0] > 230 and item[1] > 230 and item[2] > 230:
            # Fully transparent
            newData.append((255, 255, 255, 0))
        else:
            newData.append(item)
            
    img.putdata(newData)
    img.save(output_path, "PNG")
    print(f"Successfully created {output_path}")
else:
    print(f"Error: {input_path} not found.")
