import { STEPS } from "@/shared/steps";

interface ProcessStepsProps {
  currentStep: number
}

export const ProcessSteps = ({ currentStep }: ProcessStepsProps) => {
  return (
    <div className="col-span-4 space-y-6 border-r pr-8">
      {STEPS.map((step) => {

        const isCurrent = currentStep == step.id;
        const isDone = currentStep > step.id

        return (
          <div key={step.id} className={`flex items-center gap-4 ${isCurrent ? 'text-blue-600' : isDone ? 'text-blue-400' : 'text-gray-400'}`}>
            <step.icon size={20} />

            <span className={`text-sm font-bold ${isCurrent ? 'opacity-100' : 'opacity-40'}`}>{step.name}</span>
          </div>
        );
      })}
    </div>
  );
}