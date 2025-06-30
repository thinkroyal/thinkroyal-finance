import sys
import pdfplumber
import json
import re

def extract_transactions(pdf_path):
    transactions = []
    date_regex = r"\d{1,2}/\d{1,2}/\d{2,4}"

    with pdfplumber.open(pdf_path) as pdf:
        for page in pdf.pages:
            text = page.extract_text()
            if not text:
                continue
            lines = text.split("\n")

            for line in lines:
                parts = line.strip().split()
                if not parts or not re.match(date_regex, parts[0]):
                    continue

                date = parts[0]
                amount_candidates = [p for p in parts if re.match(r"^-?\$?\d+(,\d{3})*(\.\d{2})?$", p)]
                if not amount_candidates:
                    continue

                amount = amount_candidates[-1]
                amount_index = parts.index(amount)
                desc_cat = " ".join(parts[1:amount_index]).strip()
                words = desc_cat.split()

                split_idx = next((i for i in reversed(range(len(words))) if words[i].istitle() or words[i].isupper()), len(words))
                description = " ".join(words[:split_idx])
                category = " ".join(words[split_idx:])

                transactions.append({
                    "date": date,
                    "description": description,
                    "category": category,
                    "amount": amount.replace("$", "").replace(",", "")
                })

    return transactions

if __name__ == "__main__":
    pdf_path = sys.argv[1]
    print(json.dumps(extract_transactions(pdf_path)))
