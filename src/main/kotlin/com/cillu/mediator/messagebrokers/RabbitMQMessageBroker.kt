package com.cillu.mediator.messagebrokers

import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.google.gson.Gson
import com.rabbitmq.client.*
import mu.KotlinLogging
import java.io.IOException

class RabbitMQMessageBroker: IMessageBroker {
    private var connectionUrl: String //= "amqp://guest:guest@localhost:5672/"
    private var exchangeName: String //= "platform_topic"
    private var queueName: String //= "microservice1"
    private var dlqExchangeName: String //= "platform_topic.dlq"
    private var dlqQueueName: String //= "microservice1.dlq"
    private var exchangeType: String //= "topic"
    private var consumerRetryLimit: Int = 10
    private var channel: Channel
    private var logger = KotlinLogging.logger {}

    internal constructor(connectionUrl: String, exchangeName: String, queueName: String,
                exchangeType: String, consumerRetryLimit: Int)
    {
        this.connectionUrl = connectionUrl
        this.exchangeName = exchangeName
        this.queueName = queueName
        this.dlqExchangeName = "$exchangeName.dlq"
        this.dlqQueueName = "$queueName.dlq"
        this.exchangeType = exchangeType
        this.consumerRetryLimit = consumerRetryLimit
        val factory = ConnectionFactory()
        val connection = factory.newConnection(connectionUrl)
        channel = connection.createChannel()
        channel.exchangeDeclare(exchangeName, exchangeType, true);
        channel.exchangeDeclare(dlqExchangeName, exchangeType, true);
        var args: HashMap<String, Any> = HashMap()
        args["x-dead-letter-exchange"] = dlqExchangeName;
        args["x-queue-type"] = "quorum";
        args["x-delivery-limit"] = consumerRetryLimit;
        channel.queueDeclare(queueName, true, false, false, args)
        channel.queueDeclare(dlqQueueName, true, false, false, null)
    }

    override fun bind(integrationEventName: String) {
        logger.info("Binding routingKey = $integrationEventName on Queue $queueName and Exchange $exchangeName")
        channel.queueBind(queueName, exchangeName, integrationEventName)
        logger.info("Binding routingKey = $integrationEventName on Queue $dlqQueueName and Exchange $dlqExchangeName")
        channel.queueBind(dlqQueueName, dlqExchangeName, integrationEventName)
    }

    override fun consume( mediator: IMediator ) {
        logger.info("Creating ServiceBus Consumer")
        val consumer: Consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {
                val deliveryTag = envelope!!.deliveryTag
                val integrationEventName: String = envelope.routingKey ?: ""
                try
                {
                    val message = String(body!!)
                    logger.info("Received $integrationEventName with payload: $message")
                    process(mediator, integrationEventName, message)
                    channel.basicAck(deliveryTag, false)
                    logger.info("ACK sent for $integrationEventName")
                } catch (e:Exception){
                    e.printStackTrace()
                    channel.basicNack(deliveryTag, false, true)
                    logger.info("NACK sent for $integrationEventName")
                }
            }
        }
        logger.info("Created ServiceBus Consumer")
        logger.info("Listening for IntegrationEvent on queue $queueName....")
        channel.basicConsume(queueName, false, consumer);
    }

    private fun process( mediator: IMediator, integrationEventName:String, message: String){
        var integrationEvent = Gson().fromJson(message, Class.forName(integrationEventName))
        mediator.process(integrationEvent as IntegrationEvent)
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        var json = Gson().toJson(integrationEvent)
        channel.basicPublish(
            exchangeName,
            integrationEvent::class.java.name,
            null,
            json.toByteArray(),
        )
    }


}