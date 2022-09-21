package com.cillu.mediator.messagebrokers

class AwsSqsPolicy {

    companion object{
        public fun getPolicyDocument(queueArnVal:String, topicArnVal:String): String {
            val policyDocumentVal = """
            {
                "Version": "2012-10-17",
                "Statement": 
                [{
                    "Effect": "Allow",
                    "Principal": { 
                       "Service": "sns.amazonaws.com"
                    },
                    "Action": "sqs:SendMessage",
                    "Resource": "$queueArnVal",
                    "Condition": {
                       "ArnEquals": {
                                    "aws:SourceArn": "$topicArnVal"
                        }
                    }
                }]
            }
        """.trimIndent()
            println(policyDocumentVal)
            return policyDocumentVal
        }
    }
}