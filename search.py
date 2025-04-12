from fastapi import FastAPI, File, UploadFile
from pydantic import BaseModel
from google.cloud import vision
from datetime import datetime
import io
import os
import wikipedia

app = FastAPI()

# Set your Google Cloud Vision credentials
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "steadfast-pivot-456304-c7-405cf3c4bf5d.json"
client = vision.ImageAnnotatorClient()

class LandmarkResult(BaseModel):
    name: str
    confidence: float
    latitude: float = None
    longitude: float = None
    description: str = None

@app.post("/detect-landmark", response_model=LandmarkResult)
async def detect_landmark(file: UploadFile = File(...)):
    # Read uploaded image
    contents = await file.read()
    image = vision.Image(content=contents)

    # Detect landmarks
    response = client.landmark_detection(image=image)
    landmarks = response.landmark_annotations

    if not landmarks:
        return LandmarkResult(name="Unknown", confidence=0.0)

    landmark = landmarks[0]
    name = landmark.description
    confidence = landmark.score
    latitude = landmark.locations[0].lat_lng.latitude if landmark.locations else None
    longitude = landmark.locations[0].lat_lng.longitude if landmark.locations else None

    try:
        description = wikipedia.summary(name, sentences=2)
    except:
        description = "No description available."

    return LandmarkResult(
        name=name,
        confidence=confidence,
        latitude=latitude,
        longitude=longitude,
        description=description
    )
