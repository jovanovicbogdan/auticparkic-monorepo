/* eslint-disable @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-argument */

import axios, { AxiosResponse } from "axios";
import { ApiConfig } from "../config/api.config";

type HttpMethod = "get" | "post" | "delete";

export type ApiResponse = {
  status: "ok" | "error";
  data: any;
};

export default function api(
  path: string,
  method: HttpMethod,
  body?: any,
  signal?: AbortSignal
) {
  return new Promise<ApiResponse>((resolve) => {
    const requestData = {
      method,
      url: path,
      baseURL: ApiConfig.API_URL,
      data: JSON.stringify(body),
      headers: {
        "Content-Type": "application/json",
      },
      signal,
    };

    axios(requestData)
      .then((res: any) => responseHandler(res, resolve))
      .catch((err: any) => {
        const response: ApiResponse = {
          status: "error",
          data: err,
        };

        resolve(response);
      });
  });
}

export function apiForm(
  method: HttpMethod,
  path: string,
  data: FormData
): Promise<ApiResponse> {
  return new Promise((resolve) => {
    axios({
      method,
      baseURL: ApiConfig.API_URL,
      url: path,
      data,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
      .then((res: any) => responseHandler(res, resolve))
      .catch((err: any) => {
        const response: ApiResponse = {
          status: "error",
          data: err,
        };

        resolve(response);
      });
  });
}

function responseHandler(
  res: AxiosResponse<any>,
  resolve: (value: ApiResponse | PromiseLike<ApiResponse>) => void
) {
  if (res.status < 200 || res.status >= 300) {
    const response: ApiResponse = {
      status: "error",
      data: res.data,
    };

    return resolve(response);
  }

  const response: ApiResponse = {
    status: "ok",
    data: res.data,
  };

  return resolve(response);
}
