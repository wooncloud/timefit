import { Label } from '@/components/ui/label';

interface FormLabelProps {
  text: string;
  required?: boolean;
  htmlFor?: string;
}

export function FormLabel({ text, required = false, htmlFor }: FormLabelProps) {
  return (
    <Label htmlFor={htmlFor}>
      {text} {required && <span className="text-red-500">*</span>}
    </Label>
  );
}
