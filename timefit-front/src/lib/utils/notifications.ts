// timefit-front/src/lib/utils/notifications.ts

export type NotificationType = 'info' | 'success' | 'warning' | 'error';

const TOAST_CONTAINER_ID = 'global-toast-container';
const DEFAULT_TOAST_POSITION: ToastPositionClasses = 'toast-top-end'; // Default position
const DEFAULT_TOAST_DURATION = 3000; // 3 seconds

// Helper type for toast positions, can be expanded if needed
type ToastPositionClasses =
  | 'toast-top-start' | 'toast-top-center' | 'toast-top-end'
  | 'toast-middle-start' | 'toast-middle-center' | 'toast-middle-end'
  | 'toast-bottom-start' | 'toast-bottom-center' | 'toast-bottom-end';

function ensureToastContainer(position: ToastPositionClasses = DEFAULT_TOAST_POSITION): HTMLElement {
  let container = document.getElementById(TOAST_CONTAINER_ID);
  if (!container) {
    container = document.createElement('div');
    container.id = TOAST_CONTAINER_ID;
    // Apply all necessary classes for a toast container
    container.className = `toast ${position} z-50`; // Ensure high z-index
    document.body.appendChild(container);
  } else {
    // Update position if different, though this is unlikely with a single global container
    container.className = `toast ${position} z-50`;
  }
  return container;
}

/**
 * Displays a toast notification.
 *
 * @example
 * ```svelte
 * <script lang="ts">
 *   import { showToast } from '$lib/utils/notifications';
 *
 *   function handleSuccess() {
 *     showToast('Profile updated successfully!', 'success');
 *   }
 *
 *   function handleError() {
 *     showToast('Failed to save data.', 'error', 5000); // 5 seconds duration
 *   }
 *
 *   function showInfoToast() {
 *     showToast('This is an informational message.', 'info', 3000, 'toast-bottom-center');
 *   }
 * </script>
 *
 * <button on:click={handleSuccess}>Show Success Toast</button>
 * <button on:click={handleError}>Show Error Toast (5s)</button>
 * <button on:click={showInfoToast}>Show Info Toast (Bottom Center)</button>
 * ```
 */
export function showToast(
  message: string,
  type: NotificationType = 'info',
  duration: number = DEFAULT_TOAST_DURATION,
  position: ToastPositionClasses = DEFAULT_TOAST_POSITION
): void {
  if (typeof window === 'undefined') return; // Guard for SSR

  const container = ensureToastContainer(position);

  const toastElement = document.createElement('div');
  // Base alert class and type-specific class
  toastElement.className = `alert alert-${type} shadow-lg`;

  // Icon (optional, but good for visual distinction)
  // SVGs can be more complex, for now, a simple span or placeholder
  let iconHtml = '';
  // Basic SVGs - these should ideally be more robust or configurable
  switch (type) {
    case 'success':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`;
      break;
    case 'warning':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>`;
      break;
    case 'error':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`;
      break;
    case 'info':
    default:
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-current shrink-0 h-6 w-6"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`;
      break;
  }

  toastElement.innerHTML = `
    ${iconHtml}
    <span>${message}</span>
  `;

  container.appendChild(toastElement);

  // Auto-remove toast
  setTimeout(() => {
    toastElement.remove();
    // Optional: if container is empty, remove it.
    // However, for toasts, the container usually stays.
    // if (container.children.length === 0) {
    //   container.remove();
    // }
  }, duration);
}

// Example of how it might be used (not part of the file, just for testing idea):
// showToast('Profile updated successfully!', 'success');
// showToast('Warning: Low disk space.', 'warning', 5000);
// showToast('An error occurred.', 'error');
// showToast('New message received.', 'info', 3000, 'toast-bottom-center');

// (Existing code for NotificationType, showToast, etc. should remain above)

const ALERT_MODAL_ID = 'global-alert-modal';
const ALERT_MODAL_CONTENT_ID = 'global-alert-modal-content';
const ALERT_MODAL_TITLE_ID = 'global-alert-modal-title';
const ALERT_MODAL_MESSAGE_ID = 'global-alert-modal-message';
const ALERT_MODAL_ICON_ID = 'global-alert-modal-icon';

function ensureAlertModal(): HTMLElement {
  if (typeof window === 'undefined') {
    // This function creates DOM elements, so it should not run in SSR.
    // Return a dummy element or throw an error, or ensure it's only called client-side.
    // For simplicity in this context, we'll assume it's called client-side.
    // A more robust solution might involve a Svelte component.
    throw new Error("ensureAlertModal cannot be called server-side.");
  }

  let modal = document.getElementById(ALERT_MODAL_ID) as HTMLDialogElement | null;

  if (!modal) {
    modal = document.createElement('dialog');
    modal.id = ALERT_MODAL_ID;
    modal.className = 'modal modal-bottom sm:modal-middle'; // Responsive positioning

    const modalBox = document.createElement('div');
    modalBox.className = 'modal-box'; // Apply DaisyUI modal-box styling

    // Icon Placeholder
    const iconSpan = document.createElement('span');
    iconSpan.id = ALERT_MODAL_ICON_ID;
    // SVG will be set by showAlert

    // Title
    const titleElement = document.createElement('h3');
    titleElement.id = ALERT_MODAL_TITLE_ID;
    titleElement.className = 'text-lg font-bold'; // DaisyUI typography for title

    // Message
    const messageElement = document.createElement('p');
    messageElement.id = ALERT_MODAL_MESSAGE_ID;
    messageElement.className = 'py-4'; // DaisyUI padding

    // Modal Action (for the close button)
    const modalAction = document.createElement('div');
    modalAction.className = 'modal-action';

    const form = document.createElement('form');
    form.method = 'dialog'; // Allows button inside to close the dialog

    const closeButton = document.createElement('button');
    closeButton.className = 'btn'; // DaisyUI button styling
    closeButton.textContent = 'OK';

    form.appendChild(closeButton);
    modalAction.appendChild(form);

    modalBox.appendChild(iconSpan); // Icon will be prepended or managed by showAlert
    modalBox.appendChild(titleElement);
    modalBox.appendChild(messageElement);
    modalBox.appendChild(modalAction);

    modal.appendChild(modalBox);
    document.body.appendChild(modal);
  }
  return modal;
}

/**
 * Displays a modal alert.
 *
 * @example
 * ```svelte
 * <script lang="ts">
 *   import { showAlert } from '$lib/utils/notifications';
 *
 *   function showSimpleAlert() {
 *     showAlert('This is a basic information alert.');
 *   }
 *
 *   function showSuccessAlert() {
 *     showAlert('Your operation was completed successfully!', 'success', 'Success!');
 *   }
 *
 *   function showErrorAlert() {
 *     showAlert('An unexpected error occurred. Please try again.', 'error', 'Operation Failed');
 *   }
 * </script>
 *
 * <button on:click={showSimpleAlert}>Show Info Alert</button>
 * <button on:click={showSuccessAlert}>Show Success Alert</button>
 * <button on:click={showErrorAlert}>Show Error Alert</button>
 * ```
 */
export function showAlert(
  message: string,
  type: NotificationType = 'info',
  title?: string
): void {
  if (typeof window === 'undefined') return; // Guard for SSR

  const modal = ensureAlertModal() as HTMLDialogElement;
  const titleElement = document.getElementById(ALERT_MODAL_TITLE_ID)!;
  const messageElement = document.getElementById(ALERT_MODAL_MESSAGE_ID)!;
  const iconElement = document.getElementById(ALERT_MODAL_ICON_ID)!;
  const modalBox = modal.querySelector('.modal-box');

  // Determine title based on type if not provided
  let displayTitle = title;
  if (!displayTitle) {
    switch (type) {
      case 'success': displayTitle = 'Success'; break;
      case 'warning': displayTitle = 'Warning'; break;
      case 'error':   displayTitle = 'Error';   break;
      case 'info':
      default:        displayTitle = 'Information'; break;
    }
  }

  titleElement.textContent = displayTitle;
  messageElement.textContent = message;

  // Update icon and alert styling within modal-box (optional, but good for consistency)
  // This assumes we want to style the modal-box like an alert.
  // Alternatively, an actual alert component could be placed inside modal-box.
  // For simplicity, we'll color the title/icon area or add an icon.

  let iconHtml = '';
  let titleColorClass = ''; // To color the title, e.g. text-success, text-error

  switch (type) {
    case 'success':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-success shrink-0 h-8 w-8 mr-2" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`;
      titleColorClass = 'text-success';
      break;
    case 'warning':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-warning shrink-0 h-8 w-8 mr-2" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>`;
      titleColorClass = 'text-warning';
      break;
    case 'error':
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" class="stroke-error shrink-0 h-8 w-8 mr-2" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`;
      titleColorClass = 'text-error';
      break;
    case 'info':
    default:
      iconHtml = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-info shrink-0 h-8 w-8 mr-2"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`;
      titleColorClass = 'text-info';
      break;
  }
  iconElement.innerHTML = iconHtml;

  // Remove previous color classes from title and add new one
  titleElement.classList.remove('text-info', 'text-success', 'text-warning', 'text-error');
  if (titleColorClass) {
    titleElement.classList.add(titleColorClass);
  }

  // If modalBox exists, ensure it has appropriate classes (e.g. for consistent header styling with icon)
  if (modalBox) {
      // Prepend icon to modalBox, before the title, if not already structured that way
      // For now, the iconSpan is a direct child of modalBox, which is fine.
      // We could also embed an <div class="alert alert-info"> inside modal-box if we want the full alert background.
      // Current approach: icon + title + message + actions.
  }

  if (modal && typeof modal.showModal === 'function') {
    modal.showModal();
  } else {
    console.error("Alert modal element not found or showModal is not a function.");
  }
}

// Example of how it might be used (not part of the file):
// showAlert('Your action was successful!', 'success', 'Success!');
// showAlert('Something went wrong.', 'error');
