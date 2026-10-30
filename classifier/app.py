"""Industry classifier: TF-IDF + logistic regression over a small seed dataset.

POST /classify {"text": "..."} -> {"industry": "...", "confidence": 0.0-1.0}
Add examples to SEED (or load your own labeled data) to improve accuracy.
"""
from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline

SEED = {
    "healthcare": [
        "hospital patient records and clinical scheduling",
        "telemedicine platform for doctors and nurses",
        "dental clinic appointment booking and billing",
        "pharmacy prescription management and patient care",
        "electronic health records for medical practices",
    ],
    "finance": [
        "online banking and payments platform",
        "investment portfolio management for wealth advisors",
        "lending and credit scoring for small businesses",
        "insurance claims processing and underwriting",
        "accounting and invoicing for fintech startups",
    ],
    "retail": [
        "e-commerce storefront with product catalog and checkout",
        "point of sale system for clothing stores",
        "online grocery delivery and shopping cart",
        "inventory and order management for merchants",
        "customer loyalty rewards for retail shops",
    ],
    "education": [
        "learning management system for schools and universities",
        "online courses and student assessments",
        "classroom gradebook for teachers and parents",
        "tutoring marketplace for students",
        "campus enrollment and admissions portal",
    ],
    "manufacturing": [
        "factory production line monitoring and maintenance",
        "supply chain and procurement for industrial parts",
        "quality control inspections on the assembly line",
        "warehouse and logistics for manufacturers",
        "machine telemetry and predictive maintenance for plants",
    ],
}

texts = [t for ts in SEED.values() for t in ts]
labels = [label for label, ts in SEED.items() for _ in ts]
model = make_pipeline(TfidfVectorizer(ngram_range=(1, 2), sublinear_tf=True), LogisticRegression(max_iter=1000, C=10))
model.fit(texts, labels)

app = FastAPI(title="industry-classifier")


class ClassifyRequest(BaseModel):
    text: str


@app.post("/classify")
def classify(req: ClassifyRequest):
    probs = model.predict_proba([req.text])[0]
    best = probs.argmax()
    return {"industry": model.classes_[best], "confidence": round(float(probs[best]), 3)}


@app.get("/health")
def health():
    return {"status": "ok"}
