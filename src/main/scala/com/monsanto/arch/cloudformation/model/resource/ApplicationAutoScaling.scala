package com.monsanto.arch.cloudformation.model.resource

import java.time.Instant

import com.monsanto.arch.cloudformation.model._
import spray.json._

object ApplicationAutoScaling {
  case class ScalableTargetAction(MaxCapacity: Option[Token[Int]] = None,
                                  MinCapacity: Option[Token[Int]] = None)

  object ScalableTargetAction extends DefaultJsonProtocol {
    private type T = ScalableTargetAction

    implicit val format: JsonFormat[T] = jsonFormat2(apply)
  }

  case class ScheduledAction(EndTime: Option[Instant],
                             ScalableTargetAction: Option[ScalableTargetAction],
                             Schedule: ScheduleExpression,
                             ScheduledActionName: Token[String],
                             StartTime: Option[Instant])

  object ScheduledAction extends DefaultJsonProtocol {
    private type T = ScheduledAction
    implicit val format: JsonFormat[T] = jsonFormat5(apply)
  }

  sealed trait ScalingType extends Product with Serializable

  object ScalingType extends DefaultJsonProtocol {
    private type T = ScalingType
    case object StepScaling extends T
    case object TargetTrackingScaling extends T
    val values = Seq(StepScaling, TargetTrackingScaling)
    implicit val format: JsonFormat[T] = new EnumFormat[T](values)
  }

  sealed trait AdjustmentType extends Product with Serializable

  object AdjustmentType extends DefaultJsonProtocol {
    private type T = AdjustmentType
    case object ChangeInCapacity extends T
    case object ExactCapacity extends T
    case object PercentChangeInCapacity extends T
    val values = Seq(ChangeInCapacity, ExactCapacity, PercentChangeInCapacity)
    implicit val format: JsonFormat[T] = new EnumFormat[T](values)
  }

  case class StepAdjustment(MetricIntervalLowerBound: Option[Double] = None,
                            MetricIntervalUpperBound: Option[Double] = None,
                            ScalingAdjustment: Int) {
    assert(MetricIntervalLowerBound.nonEmpty || MetricIntervalUpperBound.nonEmpty)
  }

  object StepAdjustment extends DefaultJsonProtocol {
    private type T = StepAdjustment
    implicit val format: JsonFormat[T] = jsonFormat3(apply)
  }

  case class StepScalingPolicyConfiguration(AdjustmentType: Option[AdjustmentType] = None,
                                            Cooldown: Option[Int] = None,
                                            MetricAggregationType: Option[`AWS::CloudWatch::Alarm::Statistic`] = None,
                                            MinAdjustmentMagnitude: Option[Int] = None,
                                            StepAdjustments: Option[Seq[StepAdjustment]] = None) {
    import `AWS::CloudWatch::Alarm::Statistic`._
    MetricAggregationType foreach (t => assert(List(Average, Maximum, Minimum) contains t))
  }

  object StepScalingPolicyConfiguration extends DefaultJsonProtocol {
    private type T = StepScalingPolicyConfiguration
    implicit val format: JsonFormat[T] = jsonFormat5(apply)
  }

  case class CustomizedMetricSpecification(Dimensions: Option[Seq[`AWS::CloudWatch::Alarm::Dimension`]] = None,
                                           MetricName: String,
                                           Namespace: `AWS::CloudWatch::Alarm::Namespace`,
                                           Statistic: `AWS::CloudWatch::Alarm::Statistic`,
                                           Unit: Option[`AWS::CloudWatch::Alarm::Unit`] = None)

  object CustomizedMetricSpecification extends DefaultJsonProtocol {
    implicit val format: JsonFormat[CustomizedMetricSpecification] = jsonFormat5(apply)
  }

  sealed trait PredefinedMetricType
  object PredefinedMetricType extends DefaultJsonProtocol {
    object DynamoDBReadCapacityUtilization extends PredefinedMetricType
    object DynamoDBWriteCapacityUtilization extends PredefinedMetricType
    object ALBRequestCountPerTarget extends PredefinedMetricType
    object RDSReaderAverageCPUUtilization extends PredefinedMetricType
    object RDSReaderAverageDatabaseConnections extends PredefinedMetricType
    object EC2SpotFleetRequestAverageCPUUtilization extends PredefinedMetricType
    object EC2SpotFleetRequestAverageNetworkIn extends PredefinedMetricType
    object EC2SpotFleetRequestAverageNetworkOut extends PredefinedMetricType
    object SageMakerVariantInvocationsPerInstance extends PredefinedMetricType
    object ECSServiceAverageCPUUtilization extends PredefinedMetricType
    object ECSServiceAverageMemoryUtilization extends PredefinedMetricType

    val values = Seq(
      DynamoDBReadCapacityUtilization,
      DynamoDBWriteCapacityUtilization,
      ALBRequestCountPerTarget,
      RDSReaderAverageCPUUtilization,
      RDSReaderAverageDatabaseConnections,
      EC2SpotFleetRequestAverageCPUUtilization,
      EC2SpotFleetRequestAverageNetworkIn,
      EC2SpotFleetRequestAverageNetworkOut,
      SageMakerVariantInvocationsPerInstance,
      ECSServiceAverageCPUUtilization,
      ECSServiceAverageMemoryUtilization)

    implicit val format: JsonFormat[PredefinedMetricType] = new EnumFormat[PredefinedMetricType](values)
  }

  case class PredefinedMetricSpecification(PredefinedMetricType: PredefinedMetricType,
                                           ResourceLabel: Option[Token[String]] = None)

  case object PredefinedMetricSpecification extends DefaultJsonProtocol {
    implicit val format: JsonFormat[PredefinedMetricSpecification] = jsonFormat2(apply)
  }

  case class TargetTrackingScalingPolicyConfiguration(CustomizedMetricSpecification: Option[CustomizedMetricSpecification] = None,
                                                      DisableScaleIn: Option[Boolean] = None,
                                                      PredefinedMetricSpecification: Option[PredefinedMetricSpecification] = None,
                                                      ScaleInCooldown: Option[Token[Int]] = None,
                                                      ScaleOutCooldown: Option[Token[Int]] = None,
                                                      TargetValue: Double)

  object TargetTrackingScalingPolicyConfiguration extends DefaultJsonProtocol {
    implicit val format: JsonFormat[TargetTrackingScalingPolicyConfiguration] = jsonFormat6(apply)
  }
}

case class `AWS::ApplicationAutoScaling::ScalableTarget`(name: String,
                                                         MaxCapacity: Token[Int],
                                                         MinCapacity: Token[Int],
                                                         ResourceId: Token[String],
                                                         RoleARN: Token[String],
                                                         ScalableDimension: Token[String],
                                                         ScheduledActions: Option[Seq[ScheduledAction]] = None,
                                                         ServiceNamespace: `AWS::CloudWatch::Alarm::Namespace`,
                                                         override val Condition:  Option[ConditionRef] = None,
                                                         override val DependsOn : Option[Seq[String]] = None)
  extends Resource[`AWS::ApplicationAutoScaling::ScalableTarget`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::ApplicationAutoScaling::ScalableTarget` extends DefaultJsonProtocol {
  private type T = `AWS::ApplicationAutoScaling::ScalableTarget`
  implicit val format: JsonFormat[T] = jsonFormat10(apply)
}

case class `AWS::ApplicationAutoScaling::ScalingPolicy`(name: String,
                                                        PolicyName: Token[String],
                                                        ScalingType: ApplicationAutoScaling.ScalingType,
                                                        ResourceId: Option[Token[String]] = None,
                                                        ScalableDimension: Option[Token[String]] = None,
                                                        ScalingTargetId: Option[Token[String]] = None,
                                                        ServiceNamespace: Option[`AWS::CloudWatch::Alarm::Namespace`] = None,
                                                        StepScalingPolicyConfiguration: Option[ApplicationAutoScaling.StepScalingPolicyConfiguration] = None,
                                                        TargetTrackingScalingPolicyConfiguration: Option[ApplicationAutoScaling.TargetTrackingScalingPolicyConfiguration] = None,
                                                        override val Condition:  Option[ConditionRef] = None,
                                                        override val DependsOn : Option[Seq[String]] = None
                                                       )
  extends Resource[`AWS::ApplicationAutoScaling::ScalingPolicy`] {
  assert(
    (ScalingTargetId.nonEmpty && ResourceId.isEmpty && ScalableDimension.isEmpty && ServiceNamespace.isEmpty) ||
      (ScalingTargetId.isEmpty && ResourceId.nonEmpty && ScalableDimension.nonEmpty && ServiceNamespace.nonEmpty)
  )
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::ApplicationAutoScaling::ScalingPolicy` extends DefaultJsonProtocol {
  private type T = `AWS::ApplicationAutoScaling::ScalingPolicy`
  implicit val format: JsonFormat[T] = jsonFormat11(apply)
}
