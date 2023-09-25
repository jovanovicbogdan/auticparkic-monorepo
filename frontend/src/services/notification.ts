import { createStandaloneToast } from "@chakra-ui/react";

const { toast } = createStandaloneToast();

function notification(
  title: string,
  description: string,
  status: "success" | "error" | "warning" | "info"
) {
  toast({
    title,
    description,
    status,
    position: "top",
    duration: 4000,
    isClosable: true,
  });
}

export function successNotification(title: string, description: string) {
  notification(title, description, "success");
}

export function errorNotification(title: string, description: string) {
  notification(title, description, "error");
}

export function warningNotification(title: string, description: string) {
  notification(title, description, "warning");
}

export function infoNotification(title: string, description: string) {
  notification(title, description, "info");
}
