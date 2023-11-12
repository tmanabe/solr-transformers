#!/usr/bin/env python

import numpy
import os
import safetensors.torch
import sys

import torch

while True:
    content_length = numpy.frombuffer(os.read(sys.stdin.fileno(), 8), dtype="u8").item()
    byte_data = os.read(sys.stdin.fileno(), content_length)
    input_tensors = safetensors.torch.load(byte_data)

    output_tensors = {}
    for tensor_name, tensor in input_tensors.items():
        output_tensors[tensor_name] = torch.mean(tensor, dim=1)

    byte_data = safetensors.torch.save(output_tensors)
    os.write(sys.stdout.fileno(), numpy.array(len(byte_data), dtype="u8").tobytes())
    os.write(sys.stdout.fileno(), byte_data)
    sys.stdout.flush()
