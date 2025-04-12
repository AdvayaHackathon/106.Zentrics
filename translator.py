from fastapi import FastAPI, UploadFile, File
from google.cloud import speech, translate_v2 as translate, texttospeech
import os

app = FastAPI()

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "sonic-progress-456019-e1-68e1dc8a2c4f.json"

# --- Speech to Text ---
def speech_to_text(audio_path):
    client = speech.SpeechClient()
    with open(audio_path, "rb") as f:
        content = f.read()
    audio = speech.RecognitionAudio(content=content)
    config = speech.RecognitionConfig(
        encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
        sample_rate_hertz=44100,
        language_code="en-US",
    )
    response = client.recognize(config=config, audio=audio)
    return response.results[0].alternatives[0].transcript


# --- Translate ---
def translate_text(text, target_lang="hi"):
    client = translate.Client()
    result = client.translate(text, target_language=target_lang)
    return result["translatedText"]


# --- Text to Speech ---
def speak_text(text, lang="hi-IN"):
    client = texttospeech.TextToSpeechClient()
    synthesis_input = texttospeech.SynthesisInput(text=text)
    voice = texttospeech.VoiceSelectionParams(
        language_code=lang,
        ssml_gender=texttospeech.SsmlVoiceGender.NEUTRAL,
    )
    audio_config = texttospeech.AudioConfig(audio_encoding=texttospeech.AudioEncoding.MP3)

    response = client.synthesize_speech(input=synthesis_input, voice=voice, audio_config=audio_config)

    output_path = "output.mp3"
    with open(output_path, "wb") as out:
        out.write(response.audio_content)

    return output_path


tts_voice_map = {
    "hi": "hi-IN",
    "fr": "fr-FR",
    "es": "es-ES",
    "de": "de-DE",
    "ja": "ja-JP",
    "it": "it-IT",
    "zh": "cmn-CN",
    "en": "en-US",
}


@app.post("/translate/")
async def translate_audio(file: UploadFile = File(...), lang: str = "hi"):
    file_location = f"temp_audio.wav"
    with open(file_location, "wb") as buffer:
        buffer.write(await file.read())

    text = speech_to_text(file_location)
    translated = translate_text(text, target_lang=lang)
    voice_code = tts_voice_map.get(lang, "en-US")
    output_path = speak_text(translated, voice_code)

    return {
        "original_text": text,
        "translated_text": translated,
        "audio_file": output_path
    }
