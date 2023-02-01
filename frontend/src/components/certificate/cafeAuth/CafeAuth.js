import { Modal, Button } from "semantic-ui-react"
import { useSelector, useDispatch } from "react-redux"

import { modalActions } from "../../../store/modal"

const CafeAuth = () => {
  const dispatch = useDispatch()
  const open = useSelector((state) => state.modal.openCafeAuthModal)

  const nextPageHander = () => {
    dispatch(modalActions.toggleNearCafeListModal())
    dispatch(modalActions.toggleCafeAuthModal())
  }

  return (
    <Modal
      closeIcon
      onClose={() => dispatch(modalActions.toggleCafeAuthModal())}
      onOpen={() => dispatch(modalActions.toggleCafeAuthModal())}
      open={open}
      size="small"
      trigger={
        <Button circular size="small">
          인증하기
        </Button>
      }
    >
      <Modal.Header>카페 방문 인증</Modal.Header>
      <Modal.Description>
        <div style={{ border: "solid black 2px" }} onClick={nextPageHander}>
          <p>현재 위치로 인증하기</p>
          <p>카페에 머루르는 동안 글을 쓸 수 있어요.</p>
        </div>
        <div style={{ border: "solid black 2px" }}>
          <p>영수증으로 인증하기</p>
          <p>내일 오전 6시까지 글을 쓸 수 있어요.</p>
        </div>
      </Modal.Description>
    </Modal>
  )
}

export default CafeAuth
