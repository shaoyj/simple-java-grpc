// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: zdto.proto

package com.mylomen.grpc.pb;

public interface ByteResultOrBuilder extends
    // @@protoc_insertion_point(interface_extends:api_base.ByteResult)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 code = 1;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>string msg = 2;</code>
   * @return The msg.
   */
  java.lang.String getMsg();
  /**
   * <code>string msg = 2;</code>
   * @return The bytes for msg.
   */
  com.google.protobuf.ByteString
      getMsgBytes();

  /**
   * <code>bytes data = 3;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();
}
