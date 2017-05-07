// Generated by the protocol buffer compiler.  DO NOT EDIT!

package com.example.tutorial.nano;

@SuppressWarnings("hiding")
public interface AddressBookProtos {

  public static final class Person extends
      com.google.protobuf.nano.MessageNano {

    // enum PhoneType
    public static final int MOBILE = 0;
    public static final int HOME = 1;
    public static final int WORK = 2;

    public static final class PhoneNumber extends
        com.google.protobuf.nano.MessageNano {

      private static volatile PhoneNumber[] _emptyArray;
      public static PhoneNumber[] emptyArray() {
        // Lazily initializes the empty array
        if (_emptyArray == null) {
          synchronized (
              com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
            if (_emptyArray == null) {
              _emptyArray = new PhoneNumber[0];
            }
          }
        }
        return _emptyArray;
      }

      // optional string number = 1;
      public java.lang.String number;

      // optional .tutorial.Person.PhoneType type = 2;
      public int type;

      public PhoneNumber() {
        clear();
      }

      public PhoneNumber clear() {
        number = "";
        type = com.example.tutorial.nano.AddressBookProtos.Person.MOBILE;
        cachedSize = -1;
        return this;
      }

      @Override
      public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
          throws java.io.IOException {
        if (!this.number.equals("")) {
          output.writeString(1, this.number);
        }
        if (this.type != com.example.tutorial.nano.AddressBookProtos.Person.MOBILE) {
          output.writeInt32(2, this.type);
        }
        super.writeTo(output);
      }

      @Override
      protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (!this.number.equals("")) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeStringSize(1, this.number);
        }
        if (this.type != com.example.tutorial.nano.AddressBookProtos.Person.MOBILE) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
            .computeInt32Size(2, this.type);
        }
        return size;
      }

      @Override
      public PhoneNumber mergeFrom(
              com.google.protobuf.nano.CodedInputByteBufferNano input)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              return this;
            default: {
              if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
                return this;
              }
              break;
            }
            case 10: {
              this.number = input.readString();
              break;
            }
            case 16: {
              int value = input.readInt32();
              switch (value) {
                case com.example.tutorial.nano.AddressBookProtos.Person.MOBILE:
                case com.example.tutorial.nano.AddressBookProtos.Person.HOME:
                case com.example.tutorial.nano.AddressBookProtos.Person.WORK:
                  this.type = value;
                  break;
              }
              break;
            }
          }
        }
      }

      public static PhoneNumber parseFrom(byte[] data)
          throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
        return com.google.protobuf.nano.MessageNano.mergeFrom(new PhoneNumber(), data);
      }

      public static PhoneNumber parseFrom(
              com.google.protobuf.nano.CodedInputByteBufferNano input)
          throws java.io.IOException {
        return new PhoneNumber().mergeFrom(input);
      }
    }

    private static volatile Person[] _emptyArray;
    public static Person[] emptyArray() {
      // Lazily initializes the empty array
      if (_emptyArray == null) {
        synchronized (
            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
          if (_emptyArray == null) {
            _emptyArray = new Person[0];
          }
        }
      }
      return _emptyArray;
    }

    // optional string name = 1;
    public java.lang.String name;

    // optional int32 id = 2;
    public int id;

    // optional string email = 3;
    public java.lang.String email;

    // repeated .tutorial.Person.PhoneNumber phone = 4;
    public com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber[] phone;

    public Person() {
      clear();
    }

    public Person clear() {
      name = "";
      id = 0;
      email = "";
      phone = com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber.emptyArray();
      cachedSize = -1;
      return this;
    }

    @Override
    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
        throws java.io.IOException {
      if (!this.name.equals("")) {
        output.writeString(1, this.name);
      }
      if (this.id != 0) {
        output.writeInt32(2, this.id);
      }
      if (!this.email.equals("")) {
        output.writeString(3, this.email);
      }
      if (this.phone != null && this.phone.length > 0) {
        for (int i = 0; i < this.phone.length; i++) {
          com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber element = this.phone[i];
          if (element != null) {
            output.writeMessage(4, element);
          }
        }
      }
      super.writeTo(output);
    }

    @Override
    protected int computeSerializedSize() {
      int size = super.computeSerializedSize();
      if (!this.name.equals("")) {
        size += com.google.protobuf.nano.CodedOutputByteBufferNano
            .computeStringSize(1, this.name);
      }
      if (this.id != 0) {
        size += com.google.protobuf.nano.CodedOutputByteBufferNano
            .computeInt32Size(2, this.id);
      }
      if (!this.email.equals("")) {
        size += com.google.protobuf.nano.CodedOutputByteBufferNano
            .computeStringSize(3, this.email);
      }
      if (this.phone != null && this.phone.length > 0) {
        for (int i = 0; i < this.phone.length; i++) {
          com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber element = this.phone[i];
          if (element != null) {
            size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeMessageSize(4, element);
          }
        }
      }
      return size;
    }

    @Override
    public Person mergeFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      while (true) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            return this;
          default: {
            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
              return this;
            }
            break;
          }
          case 10: {
            this.name = input.readString();
            break;
          }
          case 16: {
            this.id = input.readInt32();
            break;
          }
          case 26: {
            this.email = input.readString();
            break;
          }
          case 34: {
            int arrayLength = com.google.protobuf.nano.WireFormatNano
                .getRepeatedFieldArrayLength(input, 34);
            int i = this.phone == null ? 0 : this.phone.length;
            com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber[] newArray =
                new com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber[i + arrayLength];
            if (i != 0) {
              java.lang.System.arraycopy(this.phone, 0, newArray, 0, i);
            }
            for (; i < newArray.length - 1; i++) {
              newArray[i] = new com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber();
              input.readMessage(newArray[i]);
              input.readTag();
            }
            // Last one without readTag.
            newArray[i] = new com.example.tutorial.nano.AddressBookProtos.Person.PhoneNumber();
            input.readMessage(newArray[i]);
            this.phone = newArray;
            break;
          }
        }
      }
    }

    public static Person parseFrom(byte[] data)
        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
      return com.google.protobuf.nano.MessageNano.mergeFrom(new Person(), data);
    }

    public static Person parseFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      return new Person().mergeFrom(input);
    }
  }

  public static final class AddressBook extends
      com.google.protobuf.nano.MessageNano {

    private static volatile AddressBook[] _emptyArray;
    public static AddressBook[] emptyArray() {
      // Lazily initializes the empty array
      if (_emptyArray == null) {
        synchronized (
            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
          if (_emptyArray == null) {
            _emptyArray = new AddressBook[0];
          }
        }
      }
      return _emptyArray;
    }

    // repeated .tutorial.Person person = 1;
    public com.example.tutorial.nano.AddressBookProtos.Person[] person;

    public AddressBook() {
      clear();
    }

    public AddressBook clear() {
      person = com.example.tutorial.nano.AddressBookProtos.Person.emptyArray();
      cachedSize = -1;
      return this;
    }

    @Override
    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
        throws java.io.IOException {
      if (this.person != null && this.person.length > 0) {
        for (int i = 0; i < this.person.length; i++) {
          com.example.tutorial.nano.AddressBookProtos.Person element = this.person[i];
          if (element != null) {
            output.writeMessage(1, element);
          }
        }
      }
      super.writeTo(output);
    }

    @Override
    protected int computeSerializedSize() {
      int size = super.computeSerializedSize();
      if (this.person != null && this.person.length > 0) {
        for (int i = 0; i < this.person.length; i++) {
          com.example.tutorial.nano.AddressBookProtos.Person element = this.person[i];
          if (element != null) {
            size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeMessageSize(1, element);
          }
        }
      }
      return size;
    }

    @Override
    public AddressBook mergeFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      while (true) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            return this;
          default: {
            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
              return this;
            }
            break;
          }
          case 10: {
            int arrayLength = com.google.protobuf.nano.WireFormatNano
                .getRepeatedFieldArrayLength(input, 10);
            int i = this.person == null ? 0 : this.person.length;
            com.example.tutorial.nano.AddressBookProtos.Person[] newArray =
                new com.example.tutorial.nano.AddressBookProtos.Person[i + arrayLength];
            if (i != 0) {
              java.lang.System.arraycopy(this.person, 0, newArray, 0, i);
            }
            for (; i < newArray.length - 1; i++) {
              newArray[i] = new com.example.tutorial.nano.AddressBookProtos.Person();
              input.readMessage(newArray[i]);
              input.readTag();
            }
            // Last one without readTag.
            newArray[i] = new com.example.tutorial.nano.AddressBookProtos.Person();
            input.readMessage(newArray[i]);
            this.person = newArray;
            break;
          }
        }
      }
    }

    public static AddressBook parseFrom(byte[] data)
        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
      return com.google.protobuf.nano.MessageNano.mergeFrom(new AddressBook(), data);
    }

    public static AddressBook parseFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      return new AddressBook().mergeFrom(input);
    }
  }
}
