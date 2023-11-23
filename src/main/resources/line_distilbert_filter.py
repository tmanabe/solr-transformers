#!/usr/bin/env python

import numpy
import os
import safetensors.torch
import sys

import torch
import transformers

MODEL = transformers.AutoModel.from_pretrained(
    "line-corporation/line-distilbert-base-japanese"
)

while True:
    content_length = numpy.frombuffer(os.read(sys.stdin.fileno(), 8), dtype="u8").item()
    byte_data = os.read(sys.stdin.fileno(), content_length)
    input_tensors = safetensors.torch.load(byte_data)

    output_tensors = {}
    for tensor_name, tensor_like in MODEL(**input_tensors).items():
        if isinstance(tensor_like, tuple):
            output_tensors[tensor_name] = torch.stack(list(tensor_like), dim=0)
        else:
            output_tensors[tensor_name] = tensor_like

    byte_data = safetensors.torch.save(output_tensors)
    os.write(sys.stdout.fileno(), numpy.array(len(byte_data), dtype="u8").tobytes())
    os.write(sys.stdout.fileno(), byte_data)
    sys.stdout.flush()
