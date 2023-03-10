import { useCallback, useState } from "react"
import { useHistory } from "react-router-dom"
import exceptionHandler from "../components/common/exceptionHandler"
import getAccessToken from "./getAccessToken"
const REST_DEFAULT_URL = process.env.REACT_APP_REST_DEFAULT_URL

const useFetch = () => {
  const history = useHistory()
  const [data, setData] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const sendRequest = useCallback(
    async (requestConfig) => {
      setIsLoading(true)
      try {
        const response = await fetch(requestConfig.url, {
          method: requestConfig.method ? requestConfig.method : "GET",
          headers: requestConfig.headers ? requestConfig.headers : {},
          body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
        })
        const responseData = await response.json()
        if (responseData.httpStatus === "OK") {
          setData(responseData.data)
        } else if (
          responseData.httpStatus === "UNAUTHORIZED" &&
          responseData.data.sign === "JWT"
        ) {
          getAccessToken({func:{sendRequest}, dataSet:{requestConfig}})
        } else {
          exceptionHandler({status:responseData.httpStatus, data:responseData.data, func:sendRequest, dataSet:requestConfig})        }
      } catch (err) {
        window.location.href='/error'
      }
      setIsLoading(false)
    },
    [history]
  )
  return { data, isLoading, sendRequest }
}

export default useFetch
