#!/usr/bin/env python

import numpy
import os
import safetensors.torch
import sys

import transformers

TOKENIZER = transformers.AutoTokenizer.from_pretrained(
    "line-corporation/line-distilbert-base-japanese", trust_remote_code=True
)

while True:
    content_length = numpy.frombuffer(os.read(sys.stdin.fileno(), 8), dtype="u8").item()
    byte_data = os.read(sys.stdin.fileno(), content_length)
    input_string = byte_data.decode("utf-8")

    output_tensors = dict(TOKENIZER(input_string, return_tensors="pt"))

    byte_data = safetensors.torch.save(output_tensors)
    os.write(sys.stdout.fileno(), numpy.array(len(byte_data), dtype="u8").tobytes())
    os.write(sys.stdout.fileno(), byte_data)
    sys.stdout.flush()
