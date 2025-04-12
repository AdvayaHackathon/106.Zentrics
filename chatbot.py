from fastapi import FastAPI
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
import torch
from huggingface_hub import login
from fastapi.middleware.cors import CORSMiddleware

# OPTIONAL: Login to Hugging Face if needed
login("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")

# Load TinyLlama model
model_id = "TinyLlama/TinyLlama-1.1B-Chat-v1.0"

tokenizer = AutoTokenizer.from_pretrained(model_id)
model = AutoModelForCausalLM.from_pretrained(
    model_id,
    torch_dtype=torch.float16,  # use torch.float32 if you're on CPU
    device_map="auto"
)

# Setup text generation pipeline
generator = pipeline(
    "text-generation",
    model=model,
    tokenizer=tokenizer,
    device_map="auto"
)

# Initialize FastAPI app
app = FastAPI()

# Allow Android frontend to access backend (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # replace * with your IP/domain in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Define request schema
class PromptRequest(BaseModel):
    prompt: str

# Define POST endpoint
@app.post("/generate")
def generate_text(request: PromptRequest):
    prompt = f"### Instruction:\n{request.prompt}\n\n### Response:\n"
    response = generator(
        prompt,
        max_new_tokens=500,
        do_sample=True,
        temperature=0.7,
        top_p=0.95,
        pad_token_id=tokenizer.eos_token_id
    )
    return {"result": response[0]["generated_text"]}
