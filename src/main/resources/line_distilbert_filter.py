#!/usr/bin/env python

import numpy
import os
import safetensors.torch
import sys


def input_bytes_from_stdin(content_len):
    bytes_list, total_len = [], 0
    while total_len < content_len:
        byte_data = os.read(sys.stdin.fileno(), content_len - total_len)
        bytes_list.append(byte_data)
        total_len += len(byte_data)
    return b''.join(bytes_list)


def input_tensors_from_stdin():
    content_len = numpy.frombuffer(os.read(sys.stdin.fileno(), 8), dtype="u8").item()
    return safetensors.torch.load(input_bytes_from_stdin(content_len))


def output_tensors_to_stdout(output_tensors):
    byte_data = safetensors.torch.save(output_tensors)
    os.write(sys.stdout.fileno(), numpy.array(len(byte_data), dtype="u8").tobytes())
    os.write(sys.stdout.fileno(), byte_data)
    sys.stdout.flush()


import torch
import transformers


MODEL = transformers.AutoModel.from_pretrained(
    "line-corporation/line-distilbert-base-japanese"
)
while True:
    input_tensors = input_tensors_from_stdin()
    output_tensors = {}
    for tensor_name, tensor_like in MODEL(**input_tensors).items():
        if isinstance(tensor_like, tuple):
            output_tensors[tensor_name] = torch.stack(list(tensor_like), dim=0)
        else:
            output_tensors[tensor_name] = tensor_like
    output_tensors_to_stdout(output_tensors)
