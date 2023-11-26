#!/usr/bin/env python

import base64
import torch
import transformers


TOKENIZER = transformers.AutoTokenizer.from_pretrained(
    "line-corporation/line-distilbert-base-japanese", trust_remote_code=True
)
MODEL = transformers.AutoModel.from_pretrained(
    "line-corporation/line-distilbert-base-japanese"
)
input_string = "LINE株式会社で[MASK]の研究・開発をしている。"
input_tensors = TOKENIZER(input_string, return_tensors="pt")
input_tensor = MODEL(**input_tensors)["last_hidden_state"]
output_tensor = torch.mean(input_tensor, dim=1)
output_tensor = torch.mean(output_tensor, dim=1)  # For simple testing
output_string = base64.b64encode(output_tensor.detach().numpy().byteswap())
print(output_string)
