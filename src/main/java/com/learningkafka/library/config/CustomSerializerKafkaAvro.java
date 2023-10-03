package com.learningkafka.library.config;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


@Slf4j
public class CustomSerializerKafkaAvro<T> implements Serializer<T> {



    private String schemaRegistryUrl;
    private String schemaSubject;
    private int schemaId;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.schemaRegistryUrl = String.valueOf(configs.get("schema.registry.url"));
        this.schemaSubject = String.valueOf(configs.get("schema.subject"));
        this.schemaId = Integer.parseInt(String.valueOf(configs.get("schema.id")));
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        CachedSchemaRegistryClient client = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            GenericData genericData = new ReflectData();

            // Inicializa o cliente fora do bloco try-with-resources
            client = new CachedSchemaRegistryClient(schemaRegistryUrl, 1000);
            Schema schema = getSchema(client);
            log.info("{}", schema);

            BinaryMessageEncoder<T> binaryMessageEncoder = new BinaryMessageEncoder<>(genericData, schema);
            binaryMessageEncoder.encode(data, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | RestClientException e) {
            // Logar o erro e relançar uma exceção personalizada se desejado
            throw new SerializationException("Error during serialization for topic '" + topic + "'", e);
        } finally {
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        // No seu caso, não é necessário fechar explicitamente aqui
    }

    private Schema getSchema(CachedSchemaRegistryClient client) throws IOException, RestClientException {
        return client.getBySubjectAndID(schemaSubject, schemaId);
    }
}
